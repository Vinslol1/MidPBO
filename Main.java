import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class Main extends Application {

    Stage window;
    Scene loginScene, userScene, productScene, salesScene, logScene;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        window = primaryStage;

        // LOGIN SCENE
        VBox loginLayout = new VBox(10);
        loginLayout.setStyle("-fx-padding: 20;");
        loginLayout.getChildren().addAll(
                new Label("Login"),
                new TextField("Username"),
                new PasswordField(),
                new Button("Login")
        );
        Button toUserScene = new Button("-> Manajemen User");
        toUserScene.setOnAction(e -> window.setScene(userScene));
        loginLayout.getChildren().add(toUserScene);
        loginScene = new Scene(loginLayout, 400, 300);

        // USER MANAGEMENT
        VBox userLayout = new VBox(10);
        userLayout.setStyle("-fx-padding: 20;");
        userLayout.getChildren().addAll(
                new Label("Manajemen Pengguna"),
                new TextField("Tambah/Update Username"),
                new PasswordField(),
                new Button("Tambah"),
                new Button("Ubah"),
                new Button("Hapus"),
                new Button("<< Kembali")
        );
        ((Button) userLayout.getChildren().get(userLayout.getChildren().size() - 1))
                .setOnAction(e -> window.setScene(loginScene));
        Button toProductScene = new Button("-> Manajemen Produk");
        toProductScene.setOnAction(e -> window.setScene(productScene));
        userLayout.getChildren().add(toProductScene);
        userScene = new Scene(userLayout, 400, 400);

        // PRODUCT MANAGEMENT
        VBox productLayout = new VBox(10);
        productLayout.setStyle("-fx-padding: 20;");
        productLayout.getChildren().addAll(
                new Label("Manajemen Produk"),
                new TextField("ID Produk"),
                new TextField("Nama Produk"),
                new TextField("Harga"),
                new Button("Tambah Produk"),
                new Button("Ubah Produk"),
                new Button("Hapus Produk"),
                new Button("<< Kembali")
        );
        ((Button) productLayout.getChildren().get(productLayout.getChildren().size() - 1))
                .setOnAction(e -> window.setScene(userScene));
        Button toSalesScene = new Button("-> Transaksi Penjualan");
        toSalesScene.setOnAction(e -> window.setScene(salesScene));
        productLayout.getChildren().add(toSalesScene);
        productScene = new Scene(productLayout, 400, 450);

        // SALES TRANSACTION
        VBox salesLayout = new VBox(10);
        salesLayout.setStyle("-fx-padding: 20;");
        salesLayout.getChildren().addAll(
                new Label("Transaksi Penjualan"),
                new TextField("Kode Barang"),
                new TextField("Jumlah"),
                new Button("Tambah ke Daftar Belanja"),
                new Label("Total: Rp 0"),
                new TextField("Pembayaran"),
                new Button("Hitung Kembalian"),
                new Label("Kembalian: Rp 0"),
                new Button("<< Kembali")
        );
        ((Button) salesLayout.getChildren().get(salesLayout.getChildren().size() - 1))
                .setOnAction(e -> window.setScene(productScene));
        Button toLogScene = new Button("-> Lihat Log");
        toLogScene.setOnAction(e -> window.setScene(logScene));
        salesLayout.getChildren().add(toLogScene);
        salesScene = new Scene(salesLayout, 400, 500);

        // LOG SCENE
        VBox logLayout = new VBox(10);
        logLayout.setStyle("-fx-padding: 20;");
        logLayout.getChildren().addAll(
                new Label("Log Aktivitas"),
                new TextArea("Log Otentikasi..."),
                new TextArea("Log Transaksi..."),
                new TextArea("Log Produk..."),
                new Button("<< Kembali")
        );
        ((Button) logLayout.getChildren().get(logLayout.getChildren().size() - 1))
                .setOnAction(e -> window.setScene(salesScene));
        logScene = new Scene(logLayout, 500, 600);

        window.setScene(loginScene);
        window.setTitle("POS Frontend Only");
        window.show();
    }
}
