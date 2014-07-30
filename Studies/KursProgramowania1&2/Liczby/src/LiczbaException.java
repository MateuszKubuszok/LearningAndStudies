class LiczbaException extends Exception {
    String ErrorInfo;

    LiczbaException (String info) {
        this.ErrorInfo = info;
    }

    public String toString () {
        return this.ErrorInfo;
    }
}
