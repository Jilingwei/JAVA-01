### Serial GC

![image-20210123163456566](/Users/jilingwei/Library/Application Support/typora-user-images/image-20210123163456566.png)

oracle官方文档当中提到：

​	串行收集器使用单个线程来执行所有垃圾收集工作，这使之相对高效，因为线程之间没有通信开销。它最适合单处理器计算机，因为它不能利用多处理器，尽管它在多处理器上对于数据集较小（最大约100 MB）的应用很有用。某些硬件和操作系统配置上默认情况下选择了串行收集器，也可以通过选项-XX：+ UseSerialGC显式启用它。

1. Serial GC特定
   1. Serial GC和应用线程的执行是串行的，在执行Serial GC的时候，会执行SWT(stop-the-world)，Serial GC使用的是分代算法，在**新生代**上，Serial使用**复制算法**进行收集，在老年代上，Serial使用**标记-压缩算法**进行收集。

