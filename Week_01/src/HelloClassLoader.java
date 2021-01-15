import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class HelloClassLoader extends ClassLoader {
    public static void main(String[] args) {
        try {
            Class<?> clazz = new HelloClassLoader().findClass("Hello");
            Method declaredMethod = clazz.getDeclaredMethod("hello");
            declaredMethod.invoke(clazz.newInstance());
        } catch (ClassNotFoundException | NoSuchMethodException |
                IllegalAccessException | InstantiationException |
                InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Class<?> findClass(String s) throws ClassNotFoundException {
        byte[] classData = loadClassData(s);
        return defineClass(s, classData, 0, classData.length);
    }

    private byte[] loadClassData(String s) throws ClassNotFoundException {
        byte[] xlassBytes;
        try {
            xlassBytes = loadFromFile(s.replace(".", File.pathSeparator) + ".xlass");
        } catch (IOException e) {
            System.err.println("error ocourred when HelloClassLoader.loadClassData");
            e.printStackTrace();
            throw new ClassNotFoundException("xlass file not found");
        }
        return xlassBytes;
    }

    private byte[] loadFromFile(String path) throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(path);
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        int nextValue;
        while ((nextValue = inputStream.read()) != -1) {
            bout.write(nextValue);
        }
        return decode(bout.toByteArray());
    }

    private byte[] decode(byte[] xlass) {
        for (int i = 0; i < xlass.length; i++) {
            xlass[i] = (byte) (255 - xlass[i]);
        }
        return xlass;
    }
}