package kr.toxicity.healthbar.api.healthbar;

public class GroupIndex {
    private int index = 0;

    public int next() {
        synchronized (this) {
            return index++;
        }
    }
    public void clear() {
        index = 0;
    }
}
