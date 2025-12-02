package Utils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {

    private static final String URL = "jdbc:sqlite:ukm.db"; // File database akan muncul di folder project

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    // Buat tabel jika belum ada (Dijalankan sekali saat aplikasi mulai)
    public static void initializeDatabase() {
        String sqlUsers = "CREATE TABLE IF NOT EXISTS users ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "username TEXT UNIQUE NOT NULL, "
                + "password TEXT NOT NULL, "
                + "role TEXT NOT NULL, "
                + "nama_lengkap TEXT)";

        String sqlAnggota = "CREATE TABLE IF NOT EXISTS anggota ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "nama TEXT NOT NULL, "
                + "nim TEXT UNIQUE NOT NULL, "
                + "telepon TEXT, "
                + "email TEXT UNIQUE NOT NULL, "
                + "status TEXT DEFAULT 'Belum Aktif')";

        String sqlKeuangan = "CREATE TABLE IF NOT EXISTS keuangan ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "nama_transaksi TEXT NOT NULL, "
                + "tipe TEXT NOT NULL, "
                + "jumlah BIGINT NOT NULL, " // Simpan angka murni
                + "pencatat TEXT)";

        String sqlKegiatan = "CREATE TABLE IF NOT EXISTS kegiatan ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "nama_kegiatan TEXT NOT NULL, "
                + "tipe TEXT NOT NULL, "
                + "lokasi TEXT, "
                + "tanggal TEXT)";

        String sqlPengumuman = "CREATE TABLE IF NOT EXISTS pengumuman ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "judul TEXT NOT NULL, "
                + "isi TEXT NOT NULL, "
                + "tanggal TEXT NOT NULL)";

        String sqlAbsensi = "CREATE TABLE IF NOT EXISTS absensi ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "username TEXT NOT NULL, "
                + "nama_kegiatan TEXT NOT NULL, "
                + "tanggal_masuk TEXT, "
                + "tanggal_keluar TEXT)";

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(sqlUsers);
            stmt.execute(sqlAnggota);
            stmt.execute(sqlKeuangan);
            stmt.execute(sqlKegiatan);
            stmt.execute(sqlPengumuman); // Tambahkan tabel pengumuman
            stmt.execute(sqlAbsensi); // Tambahkan tabel absensi

            // Seed Data Admin Default (Jika belum ada)
            createDefaultAdmin(conn);
            createDefaultDosen(conn);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createDefaultAdmin(Connection conn) {
        String sqlCheck = "SELECT count(*) FROM users WHERE username = 'admin'";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sqlCheck)) {
            if (rs.next() && rs.getInt(1) == 0) {
                String sqlInsert = "INSERT INTO users(username, password, role, nama_lengkap) VALUES(?, ?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(sqlInsert)) {
                    pstmt.setString(1, "admin");
                    pstmt.setString(2, "admin123");
                    pstmt.setString(3, "Admin");
                    pstmt.setString(4, "Administrator UKM");
                    pstmt.executeUpdate();
                    System.out.println("Admin default dibuat.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createDefaultDosen(Connection conn) {
        String sqlCheck = "SELECT count(*) FROM users WHERE username = 'dosen'";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sqlCheck)) {
            if (rs.next() && rs.getInt(1) == 0) {
                String sqlInsert = "INSERT INTO users(username, password, role, nama_lengkap) VALUES(?, ?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(sqlInsert)) {
                    pstmt.setString(1, "dosen");
                    pstmt.setString(2, "dosen123");
                    pstmt.setString(3, "Dosen");
                    pstmt.setString(4, "Dosen UKM");
                    pstmt.executeUpdate();
                    System.out.println("Dosen default dibuat.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}