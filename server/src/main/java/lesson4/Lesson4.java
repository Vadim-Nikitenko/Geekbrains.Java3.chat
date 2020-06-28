package lesson4;

public class Lesson4 {
    private static final Object mon = new Object();
    private static int num = 1;
    private static final int count = 3;

    public static void main(String[] args) {


        Thread t1 = new Thread(() -> {
            try {
                for (int i = 0; i < count; i++) {
                    synchronized (mon) {
                        while (num != 1) {
                            mon.wait();
                        }
                        System.out.println("A");
                        num = 2;
                        mon.notifyAll();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        t1.start();

        Thread t2 = new Thread(() -> {
            try {
                for (int i = 0; i < count; i++) {
                    synchronized (mon) {
                        while (num != 2) {
                            mon.wait();
                        }
                        System.out.println("B");
                        num = 3;
                        mon.notifyAll();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        t2.start();

        Thread t3 = new Thread(() -> {
            try {
                for (int i = 0; i < count; i++) {
                    synchronized (mon) {
                        while (num != 3) {
                            mon.wait();
                        }
                        System.out.println("C");
                        num = 1;
                        mon.notifyAll();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        t3.start();
    }
}
