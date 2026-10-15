# ゲームハード・周辺機器ショップ管理システム

JavaとSpring Bootを用いて開発したWebアプリケーションです。

## 主な機能
- 商品一覧の表示
- ショッピングカートへの追加・削除・数量変更
- 注文処理
- 管理者ログイン
- 商品の登録、価格・在庫の変更、販売停止・再開
- 注文履歴と売上の確認

## 使用技術
- Java
- Spring Boot
- Thymeleaf
- H2 Database
- HTML / CSS

## 起動方法（Windows）

PowerShellでプロジェクトのフォルダを開き、テスト用の管理者パスワードを設定して実行します。

```powershell
$env:ADMIN_USERNAME = "admin"
$env:ADMIN_PASSWORD = "change-this-for-local-testing"
.\mvnw.cmd spring-boot:run
```

起動後、http://localhost:8080/nazoshop にアクセスしてください。

データベースにはH2のインメモリ設定を使用しています。
管理者パスワードは環境変数で設定し、ソースコードには直接記載しません。
