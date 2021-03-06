Table Truncation Teardown
=========================

がいよう
-------
* 繰り返し実行可能で、しっかりしたテストを支えるものは何か
    * テストフィクスチャーがテストの最後に解体 (tear down) されることを保証していること
    * 不要なリソースを残しておけば、良くて性能劣化、悪くてテストの失敗やシステムクラッシュを引き起こす
        * オブジェクト
        * データベースのレコード
        * ファイル
        * コネクション
* あらゆる環境を適切に掃除することを期待された teardown コードを書くことは大変
* *Complex Teardown* (*Obscure Test*, pp.186) の末路
    * ちょっとした *Conditional Test Logic* (pp.200)
    * 最悪の場合 *Untestable Test Code* (*Hard-to-Test Code*, pp.209) になります

* RDB を使うシステムのテストには、*TRUNCATE* コマンドというアドバンテージがあります

How It Works
------------

永続的なフィクスチャーが不要なら、それぞれのテーブルに対して *TRUNCATE* コマンドを実行する。

When To Use It
--------------

* 満たすべき条件があります
    1. **本当に**全てのデータを消去したいということ
    2. テスト実行に *Database Sandbox* (pp.650) を使っていること
* *Persistent Fresh Fixture* を使っているときは 1 番の選択肢です
* *Shared Fixture* (pp.317) で使うのもよいでしょう
* トランザクションのあるデータベースを使ってない場合の *Automated Teardown* (pp.503) に類似したパターンです
    * *Delta Assertions* (pp.485) によって teardown が必須ではなくなりました

Implementation Notes
--------------------

1. 実際にどうやってデータを消すか (データベースで実行するコマンドは何?)
    * 使えるなら **TRUNCATE**、なければ **DELETE * FROM table-name**
2. 外部キー制約やトリガーをどうするか
    * Oracle には関連する行を削除する **ON DELETE CASCADE** オプションがある
    * *cascade delete* のないデータベースもある
    * schema に従って順番にデータが削除されるようにしないといけない
        * schema が変わるとこの順番も変わってしまう ?
    * テスト時は制約やトリガーを無効にすればよい
3. 使ってる ORM (OR マッパー) との一貫性をどうやって保つか
    * ORM にデータ削除をさせれば、データベースのデータだけでなく、ORM のキャッシュやオブジェクトも消せる

### Variation: Lazy Teardown

* *Shared Fixture* と一緒に使える数少ない teardown 戦略
* フィクスチャーが任意の時点で削除可能でなければなりません
* *Table Truncation Teardown* は、いつ実行しても同じ処理をするところが目的に合っています
* フィクスチャーの生成をする直前で、*trancation command* を実行するだけです

Motivating Example
------------------

*Guaranteed In-line Teardown* の例です。

 [Test]
 public void TestGetFlightsByOrigin_NoInboundFlights() {
     // Fixture Setup
     long OutboundAirport = CreateTestAirport("1OF");
     long InboundAirport = CreateTestAirport("1IF");
     FlightDto ExpFlightDto = null;
     try {
         ExpFlightDto = CreateTestFlight(OutboundAirport, InboundAirport);
         // Exercise System IList FlightsAtDestination1 =
             Facade.GetFlightsByOriginAirport( InboundAirport);
         // Verify Outcome Assert.AreEqual( 0, FlightsAtDestination1.Count );
     } finally {
         Facade.RemoveFlight( ExpFlightDto.FlightNumber );
         Facade.RemoveAirport( OutboundAirport );
         Facade.RemoveAirport( InboundAirport );
     }
 }

Refactoring Notes
-----------------

### Example: Table Truncation (Delegated) Teardown Test

 public void TestGetFlightsByOrigin_NoInboundFlight_TTTD()
 {
     // Fixture Setup
     long OutboundAirport = CreateTestAirport("1OF");
     long InboundAirport = 0;
     FlightDto ExpectedFlightDto = null;
     try
     {
         InboundAirport = CreateTestAirport("1IF");
         ExpectedFlightDto = CreateTestFlight( OutboundAirport,InboundAirport);
         // Exercise System
         IList FlightsAtDestination1 = Facade.GetFlightsByOriginAirport(InboundAirport);
         // Verify Outcome
         Assert.AreEqual(0,FlightsAtDestination1.Count);
     }
     finally
     {
         CleanDatabase();
     }
 }

### Example: Lazy Teardown Test

    public void TestGetFlightsByOrigin_NoInboundFlight_LTD()
    {
        // Lazy Teardown
        CleanDatabase();
        // Fixture Setup
        long OutboundAirport = CreateTestAirport("1OF");
        long InboundAirport = 0;
        FlightDto ExpectedFlightDto = null;
        InboundAirport = CreateTestAirport("1IF");
        ExpectedFlightDto = CreateTestFlight( OutboundAirport,InboundAirport);
        // Exercise System
        IList FlightsAtDestination1 = Facade.GetFlightsByOriginAirport(InboundAirport);
        // Verify Outcome
        Assert.AreEqual(0,FlightsAtDestination1.Count);
    }

### Example: Table Truncation Teardown Using SQL

     public static void CleanDatabase()
     {
         string[] tableToTruncate =
             new string[] {"AirPort","City","Airline_Cd","Flight"};
         IDbConnection conn = getCurrentConnection();
         IDbTransaction txn = conn.BeginTransaction();
         try {
             foreach (string eachTableToTruncate in tableToTruncate)
             {
                 TruncateTable(txn, eachTableToTruncate);
             }
             txn.Commit();
             conn.Close();
         } catch (Exception e) {
             txn.Rollback();
         } finally {
             conn.Close();
         }
     }
    
     public static void TrancateTable( IDbTransaction txn,
                                       string tableName)
     {
         const string C_DELETE_SQL = "DELETE FROM {0}";
     
         IDbCommand cmd = txn.Connection.CreateCommand();
         cmd.Transaction = txn;
         cmd.CommandText = string.Format(C_DELETE_SQL, tableName);
     
         cmd.ExecuteNonQuery();
     }

### Example: Table Truncation Teardown Using ORM

    public static void CleanDatabase() {
        ISession session = TransactionManager.Instance.CurrentSession;
        TransactionManager.Instance.BeginTransaction();
        try {
            // We need to delete only the root classes because
            // cascade rules will delete all related child entities
            session.Delete("from Airport");
            session.Delete("from City");
            session.Flush();
            TransactionManager.Instance.Commit();
        } catch (Exception e) {
            Console.Write(e);
            throw e;
        } finally {
            TransactionManager.Instance.CloseSession();
        }
    }
