import org.checkerframework.common.value.qual.StringVal;

class StringValOfArrays {
    void chars() {
        String s = "$-hello@";
        char @StringVal("$-hello@") [] chars = s.toCharArray();
        @StringVal("$-hello@") String s2 = new String(chars);
    }
}
