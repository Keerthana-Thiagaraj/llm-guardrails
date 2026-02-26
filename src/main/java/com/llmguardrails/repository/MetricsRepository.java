package com.llmguardrails.repository;

import com.llmguardrails.model.Decision;
import org.springframework.stereotype.Repository;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class MetricsRepository implements IMetricsRepository {
    
    private final ConcurrentHashMap<Decision, AtomicLong> counters = new ConcurrentHashMap<>();
    private final AtomicLong totalRequests = new AtomicLong(0);
    
    public MetricsRepository() {
        counters.put(Decision.ALLOW, new AtomicLong(0));
        counters.put(Decision.BLOCK, new AtomicLong(0));
        counters.put(Decision.REDACT, new AtomicLong(0));
    }
    
    public void recordRequest(Decision decision) {
        totalRequests.incrementAndGet();
        counters.get(decision).incrementAndGet();
    }
    
    public Long getTotalRequests() {
        return totalRequests.get();
    }
    
    public Long getCount(Decision decision) {
        return counters.getOrDefault(decision, new AtomicLong(0)).get();
    }
    
    public void reset() {
        totalRequests.set(0);
        counters.values().forEach(counter -> counter.set(0));
    }
}

