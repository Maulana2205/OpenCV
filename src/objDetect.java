import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 * Kelas ObjDetect menangani proses deteksi objek berbentuk lingkaran
 * Kelas ini melakukan pemrosesan gambar mulai dari pemuatan, pre-processing (grayscale & blur),
 * deteksi, hingga visualisasi hasil dengan menggambar batas lingkaran dan garis penghubung
 */

public class objDetect {

    /**
     * Titik masuk utama aplikasi.
     * Metode ini memuat pustaka native OpenCV, membaca gambar dari path yang ditentukan,
     * melakukan konversi warna, mengurangi noise, dan mendeteksi lingkaran.
     * Hasil deteksi divisualisasikan dan disimpan kembali ke disk
     */
    public static void main(String[] args) {
        // 1. Konfigurasi Awal & Pemuatan Library
        try {
            System.load("C:\\Users\\USER\\Downloads\\opencv\\build\\java\\x64\\opencv_java4120.dll");
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Error: Gagal memuat library native OpenCV. " + e.getMessage());
            return;
        }

        String imagePath = "C:\\Users\\USER\\Downloads\\7daea9a0-32ea-4909-bdaa-1846a9127e3c.jpg";

        // 2. Membaca Gambar
        Mat src = Imgcodecs.imread(imagePath);
        if (src.empty()) {
            System.out.println("Gagal memuat gambar. Harap periksa path file: " + imagePath);
            return;
        }

        // 3. Pre-processing Gambar
        Mat gray = new Mat();
        // Konversi BGR ke Grayscale untuk keperluan deteksi tepi
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);

        // Penerapan Median Blur untuk mengurangi noise (salt-and-pepper) sambil menjaga ketajaman tepi
        Imgproc.medianBlur(gray, gray, 5);

        // 4. Proses Deteksi Lingkaran (Hough Transform)
        Mat circles = new Mat();

        /* Parameter HoughCircles:
         * minDist: Jarak minimum antar pusat lingkaran (rows/25).
         * param1: Threshold atas untuk detektor tepi Canny.
         * param2: Threshold akumulator (semakin kecil, semakin sensitif/banyak deteksi palsu).
         */
        double minDist = (double) gray.rows() / 25;
        double param1 = 150;
        double param2 = 18;
        int minRadius = 25;
        int maxRadius = 55;

        Imgproc.HoughCircles(gray, circles, Imgproc.HOUGH_GRADIENT, 1.0,
                minDist, param1, param2, minRadius, maxRadius);

        // 5. Visualisasi Hasil
        System.out.println("Jumlah koin terdeteksi: " + circles.cols());

        for (int i = 0; i < circles.cols(); i++) {
            double[] c = circles.get(0, i);
            Point center = new Point(Math.round(c[0]), Math.round(c[1]));
            int radius = (int) Math.round(c[2]);

            // Menggambar titik pusat (Merah)
            Imgproc.circle(src, center, 3, new Scalar(0, 0, 255), -1, 8, 0);
            // Menggambar keliling lingkaran (Hijau)
            Imgproc.circle(src, center, radius, new Scalar(0, 255, 0), 2, 8, 0);
        }

        // Menghubungkan titik-titik pusat objek
        connectCenters(src, circles);

        // 6. Penyimpanan Hasil
        String outputPath = "detected_coins+lines.jpg";
        Imgcodecs.imwrite(outputPath, src);
        System.out.println("Gambar hasil deteksi berhasil disimpan sebagai '" + outputPath + "'");
    }

    /**
     * Menggambar garis lurus yang menghubungkan titik pusat dari setiap lingkaran yang terdeteksi.
     * Metode ini melakukan iterasi melalui matriks lingkaran dan menggambar garis
     * dari pusat lingkaran ke-i menuju pusat lingkaran ke-(i+1) secara berurutan
     * @param img     Matriks gambar tujuan (source image) di mana garis visualisasi akan digambar.
     * Gambar ini akan dimodifikasi secara langsung (in-place).
     * @param circles Matriks keluaran dari {@code HoughCircles} yang berisi data lingkaran
     * (x, y, radius). Setiap kolom merepresentasikan satu lingkaran.
     */
    private static void connectCenters(Mat img, Mat circles) {
        int totalCircles = circles.cols();

        // Validasi: Membutuhkan minimal 2 titik untuk membuat garis
        if (totalCircles < 2) {
            return;
        }

        for (int i = 0; i < totalCircles - 1; i++) {
            // Mengambil koordinat titik saat ini (i)
            double[] c1 = circles.get(0, i);
            Point p1 = new Point(Math.round(c1[0]), Math.round(c1[1]));

            // Mengambil koordinat titik berikutnya (i+1)
            double[] c2 = circles.get(0, i + 1);
            Point p2 = new Point(Math.round(c2[0]), Math.round(c2[1]));

            // Menggambar garis Biru (RGB: 0, 0, 255 -> OpenCV Scalar: B=255, G=0, R=0)
            Imgproc.line(img, p1, p2, new Scalar(255, 0, 0), 2);
        }
        /* // OPSI 2: MENGHUBUNGKAN SEMUA KE SEMUA (Mesh / Jaring)
        for (int i = 0; i < totalCircles; i++) {
            for (int j = i + 1; j < totalCircles; j++) {
                double[] c1 = circles.get(0, i);
                Point p1 = new Point(Math.round(c1[0]), Math.round(c1[1]));

                double[] c2 = circles.get(0, j);
                Point p2 = new Point(Math.round(c2[0]), Math.round(c2[1]));

                Imgproc.line(img, p1, p2, new Scalar(255, 255, 0), 1); // Warna Cyan
            }
        }
        */
    }
}