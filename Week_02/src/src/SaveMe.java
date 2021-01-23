public class SaveMe {

    public static SaveMe saveMe;

    public static void main(String[] args) throws InterruptedException {
        saveMe = new SaveMe();
        saveMe = null; // 取消引用，经过可达性分析，上面new出来的对象不再可达GC Root
        System.gc(); // 第一次GC，会执行finalize方法
        Thread.sleep(1000);
        if (saveMe == null) {
            System.out.println("对象为null");
        } else {
            System.out.println("对象不为null");
        }
        // 经过上面的过程，对象已经自救了，这里再次将其引用置空
        saveMe = null;
        System.gc(); // 不会再执行finalize方法，没有机会自救了
        Thread.sleep(1000);
        if (saveMe == null) {
            System.out.println("对象为null");
        } else {
            System.out.println("对象不为null");
        }
    }

    // finalize方法全局只会执行一次
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        saveMe = this; // 进行自救
    }
}
