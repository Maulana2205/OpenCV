import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class objDetect {
    public static void main(String[] args) {
        // 1. Memuat Library Native OpenCV
        System.load("C:\\Users\\ZNET\\Downloads\\opencv\\build\\java\\x64\\opencv_java4120.dll");

        String imagePath = "C:\\Users\\ZNET\\Downloads\\d309d1a9-9ae7-4006-b081-1bb561c8555c.jpg";

        // 2. Membaca Gambar
        Mat src = Imgcodecs.imread(imagePath);
        if (src.empty()) {
            System.out.println("Gagal memuat gambar. Periksa path file.");
            return;
        }

        // 3. Pre-processing (Sesuai dokumentasi OpenCV)
        // Konversi ke Grayscale (Abu-abu)
        Mat gray = new Mat();
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);

        // Menggunakan Median Blur untuk mengurangi noise namun menjaga tepi koin
        // Ukuran kernel 5 biasanya cukup baik untuk koin
        Imgproc.medianBlur(gray, gray, 5);

        // 4. Deteksi Lingkaran menggunakan HoughCircles
        Mat circles = new Mat();

        // PARAMETER TUNING:
        // dp = 1: Resolusi akumulator sama dengan gambar.
        // minDist: Jarak minimum antar pusat lingkaran.
        double minDist = (double) gray.rows() / 25;//16

        // param1: Threshold tinggi untuk Canny edge detector (biasanya 100-200).
        // param2: Threshold akumulator. Semakin KECIL, semakin banyak lingkaran palsu terdeteksi.
        double param1 = 100;
        double param2 = 26;

        // minRadius & maxRadius: Batas ukuran koin.
        int minRadius = 15;//15
        int maxRadius = 60;//60

        Imgproc.HoughCircles(gray, circles, Imgproc.HOUGH_GRADIENT, 1.0,
                minDist,
                param1,
                param2,
                minRadius,
                maxRadius);

        // 5. Menampilkan Hasil
        System.out.println("Jumlah koin terdeteksi: " + circles.cols());

        // Menggambar lingkaran pada gambar asli
        for (int i = 0; i < circles.cols(); i++) {
            double[] c = circles.get(0, i);
            Point center = new Point(Math.round(c[0]), Math.round(c[1]));
            int radius = (int) Math.round(c[2]);

            // Gambar titik pusat (Warna Merah)
            Imgproc.circle(src, center, 1, new Scalar(0, 0, 255), 3, 8, 0);
            // Gambar keliling lingkaran (Warna Hijau)
            Imgproc.circle(src, center, radius, new Scalar(0, 255, 0), 2, 8, 0);
        }

        // Menyimpan hasil gambar output
        Imgcodecs.imwrite("detected_coins12.jpg", src);
        System.out.println("Gambar hasil deteksi disimpan sebagai 'detected_coins.jpg'");
    }
}