package Util;

public interface RowMapper<U> {
    Object[] map(U item);
}
