public enum UserStatus {
    OFFLINE (0), ONLINE (1);

    private final int status;

    UserStatus(int id) {
        this.status = id;
    }

    public int getStatus() {
        return status;
    }
}
