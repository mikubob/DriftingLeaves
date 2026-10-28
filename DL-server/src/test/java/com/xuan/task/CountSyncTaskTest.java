package com.xuan.task;

import com.xuan.constant.RedisConstant;
import com.xuan.service.CounterBatchWriter;
import com.xuan.task.CounterSyncCoordinator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.script.RedisScript;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/** Retains the original snapshot/new-increment regressions; real Lua/transactions are tested separately. */
class CountSyncTaskTest {
    private final CounterBatchWriter writer = mock(CounterBatchWriter.class);
    private final StringRedisTemplate redis = mock(StringRedisTemplate.class);
    private final HashOperations<String,Object,Object> counts = mock(HashOperations.class);
    private final ValueOperations<String,String> locks = mock(ValueOperations.class);
    private final AtomicLong pending = new AtomicLong();

    @ParameterizedTest @ValueSource(booleans = {true,false})
    void preservesRequestsArrivingDuringDatabaseWrite(boolean likes) {
        Runnable task = task(likes,5);
        doAnswer(i -> { pending.addAndGet(3); return null; }).when(writer).apply(anyString(),eq(1L),anyString(),eq(5L));
        task.run();
        assertEquals(3,pending.get());
    }
    @ParameterizedTest @ValueSource(booleans = {true,false})
    void preservesRequestsArrivingAfterZeroSnapshot(boolean likes) {
        Runnable task = task(likes,0);
        pending.set(3);
        task.run();
        assertEquals(3,pending.get());
        verifyNoInteractions(writer);
    }
    @ParameterizedTest @ValueSource(booleans = {true,false})
    void preservesRequestsArrivingAfterDecrementReachesZero(boolean likes) {
        Runnable task = task(likes,5);
        task.run();
        pending.addAndGet(3);
        assertEquals(3,pending.get());
        verify(counts,never()).delete(anyString(),any());
    }
    @ParameterizedTest @ValueSource(booleans = {true,false})
    void leavesPendingCountsForRetryWhenDatabaseWriteFails(boolean likes) {
        Runnable task = task(likes,5);
        doThrow(new IllegalStateException("database unavailable")).when(writer).apply(anyString(),anyLong(),anyString(),anyLong());
        task.run();
        assertEquals(5,pending.get());
    }
    @Test void syncsUnlikesAndPreservesNewLikes() {
        Runnable task = task(true,-5);
        pending.set(-3);
        task.run();
        assertEquals(2,pending.get());
        verify(writer).apply(eq("like"),eq(1L),anyString(),eq(-5L));
    }
    private Runnable task(boolean likes,long snapshot) {
        pending.set(snapshot);
        when(redis.opsForValue()).thenReturn(locks);
        when(locks.setIfAbsent(anyString(),anyString(),any(java.time.Duration.class))).thenReturn(true);
        when(redis.opsForHash()).thenReturn(counts);
        when(counts.keys(anyString())).thenAnswer(i -> i.getArgument(0,String.class).endsWith(":batches") ? Set.of() : Set.of("1"));
        doAnswer(i -> {
            RedisScript<?> script=i.getArgument(0);
            if (script.getResultType()==String.class) return snapshot==0 ? null : "batch|"+snapshot;
            if (script.getScriptAsString().contains("HINCRBY")) pending.addAndGet(-snapshot);
            return 1L;
        }).when(redis).execute(any(RedisScript.class),anyList(),any(Object[].class));
        CounterSyncCoordinator service=new CounterSyncCoordinator(redis,writer);
        return likes ? new LikeCountSyncTask(service)::syncLikeCountToMySQL : new ViewCountSyncTask(service)::syncViewCountToMySQL;
    }
}
