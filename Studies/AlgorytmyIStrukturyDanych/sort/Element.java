public class Element {
    private int CurrentKey;

    private int Moved;

    private int OriginalKey;

    private int Value;

    public Element (int key, int value) {
        this.CurrentKey = key;
        this.OriginalKey = key;
        this.Moved = 0;
        this.Value = value;
    }

    public int currentlyAt () {
        return this.CurrentKey;
    }

    public int getValue () {
        return this.Value;
    }

    public int moved () {
        return this.Moved;
    }

    public void movedTo (int key) {
        this.CurrentKey = key;
        this.Moved++;
    }

    public int originallyAt () {
        return this.OriginalKey;
    }
}
