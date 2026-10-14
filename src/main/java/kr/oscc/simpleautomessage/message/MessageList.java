package kr.oscc.simpleautomessage.message;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class MessageList {
    private final String id;
    private final boolean enabled;
    private final long intervalSeconds;
    private final int expiry;
    private final boolean random;
    private final List<String> messages;
    private int cursor;
    private int cycles;

    public MessageList(String id, boolean enabled, long intervalSeconds, int expiry, boolean random, List<String> messages) {
        this.id = id;
        this.enabled = enabled;
        this.intervalSeconds = Math.max(1L, intervalSeconds);
        this.expiry = expiry;
        this.random = random;
        this.messages = List.copyOf(messages);
    }

    public String id() { return id; }
    public boolean enabled() { return enabled; }
    public long intervalSeconds() { return intervalSeconds; }
    public int expiry() { return expiry; }
    public boolean random() { return random; }
    public List<String> messages() { return messages; }

    public boolean expired() {
        return expiry >= 0 && cycles >= expiry;
    }

    public String next() {
        if (messages.isEmpty()) return null;
        int index;
        if (random) {
            index = ThreadLocalRandom.current().nextInt(messages.size());
        } else {
            index = cursor;
            cursor = (cursor + 1) % messages.size();
        }
        cycles++;
        return messages.get(index);
    }

    public String at(int oneBasedIndex) {
        if (oneBasedIndex < 1 || oneBasedIndex > messages.size()) return null;
        return messages.get(oneBasedIndex - 1);
    }
}
