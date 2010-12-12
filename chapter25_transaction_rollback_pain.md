Transaction Rollback Pain
=========================

John Hurst から、''Transaction Rollback Teardown'' を使っていて経験した
問題についてメールをもらいました。

私達は、データベースの結合テストに ''Transaction Rollback Teardown'' を
使っていました。[TheServerSide](http://www.theserverside.com) での議論
で、Rod Johnson もそれを支持していたからです。
([たぶんこのスレッド](http://www.theserverside.com/news/thread.tss?thread_id=33215))

Rod Johnson の主張を私なりにまとめると、次のテストのために新しくトラン
ザクションを始めるよりロールバックするほうが速いので、このパターンはパ
フォーマンスのために使うということでした。実際に、今までの方法より速く
なっていました。Spring の
''AbstractTransactionalData-SourceSpringContextTest'' には、このパター
ンのための機能が備わっています。

しかし、数ヶ月後このパターンの使用を諦めました。このパターンを使っていて発生した問題を紹介します。

1. テストの独立性を失う

このパターンのテストの実装は、データベースが初期状態であること、ロールバックが元の状態に戻してくれることを想定したものになりました。
このパターンを諦めた今のテストでは、それぞれの基底クラスの setUp メソッドにより、いくつかの初期データの投入された既知の状態となるようなモデルになっています。

2. テストが失敗したときのデータベースの中身が見れない

テストが失敗したときは、普通はデータベースの中身を確認したいと思うでしょう。
ロールバックした後では確認ができないのでバグを見つけるのは大変です。

3. テストコード中の意図しないコミットに注意しないといけない

テストコードは宣言的なトランサクション管理をされているので、驚くようなことは起きません。
しかし、テストのセットアップでテーブルを削除したり、シーケンスを初期化するために再作成したりすることは時々あります。
こういった DDL 的なものは保留中のトランザクションをコミットしてしまい、プログラマーを混乱させます。

4. コミットの必要なテストを組み合せることが大変
開発の後半に、PL/SQL のストアドプロシージャとそのテストを追加しました。
いくつかのストアドプロシージャには、明示的にコミットが含まれていました。
これらのテストは、常にロールバックされた状態を期待する JUnit suite には含めることができませんでした。

言葉の使い方が書籍に書いてあるのと違ってたりしてすみません。私の経験は
Spring の環境で経験したごく限定されたものですし、"Spring" のやり方に従
うことをお勧めします

これまでに述べた制限には、いろんな回避策があるとは思いますが、私達のチー
ムにはその恩恵よりも、トラブルの方が多かったようです。

とはいえ、このパターンはこの本のパターンカタログに含められるべきですし、
私達の結論は注意書き程度で、全ての人に適用されるべきものでもありません。