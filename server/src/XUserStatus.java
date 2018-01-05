public enum XUserStatus {
    OFFLINE (0), ONLINE (1);

    private final int status;

    XUserStatus(int id) {
        this.status = id;
    }

    public int getStatus() {
        return status;
    }
}
