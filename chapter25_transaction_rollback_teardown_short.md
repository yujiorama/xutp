Transaction Rollback Teardown
=============================

がいよう
-------
* 繰り返し実行可能で (ry
* あらゆる環境を適切に掃除することを (ry
* 未コミットのデータベースへの変更は、ロールバックで削除できます

How It Works
------------

1. 新しいトランザクションを開始します
2. フィクスチャーを生成します
3. SUT のテストを実行します
4. テストの出力を検証します
5. トランザクションをロールバックします

When to Use It
--------------

* *Fresh Fixture* (pp.311) とロールバック可能なデータベースを使っているときに使えるパターンです
* SUT が、トランザクション中で呼ばれることを想定したメソッドを公開しているような状況があります
    * このメソッドは、トランザクションを開始しないし、コミットもしません
    * トランザクションは、*Humble Transaction Controller* (pp.695) によって提供されます
    * テスト駆動開発をしているなら、*Transaction Rollback Teardown* を適用すると最終的にこういう設計になります

* 実際のデータベースでテストを実行するときの注意点
    * データベースアクセスのないテストに対して 50 倍時間がかかります
    * *In-Memory Database* (pp.551) に置き換えない限り *Slow Tests* (pp.253) になります
    * トランザクション機能に依存してるので、ACID をサポートしてない単純な Fake Database では荷が重いです
    * どこかでトランザクションがコミットされてしまっても何もできません

よく分からない箇所でコミットされるせいで混乱した例を紹介しましょう。
[Transaction Rollback Pain](chapter25_transaction_rollback_teardown_pain.md)

Implementation Notes
--------------------

* 本体だけでこのパターンをサポートしている xUnit ファミリーはあまり多くありません
* 真剣に考慮すべきは、SUT のトランザクション不要なメソッドにアクセスさせることです
    * ほとんどのドメインオブジェクトはトランザクション不要なので、ドメインオブジェクトのユニットテストではそれほど問題ではないです
* *Subcutaneous Test* (pp.337) を書くときに問題になるでしょう
    * *Service Facade* [CJ2EEP] がトランザクション管理をするからです
    * トランザクション不要なメソッドを公開するために、*Humble Transaction Controller* パターンへのリファクタリングが必要かもしれません
    * トランザクション不要なメソッドを、*Decorator* [GOF] に委譲することも可能です (*Poor Mans Humble Object* と呼ばれる方法)

* クライアントから見えてないメソッドがあるなら、テストのためにこれを公開しなければなりません
    * *Test-Specific Subclass* (pp.579) によって間接的に公開することもできます
    * *Extract Testable Component* (pp.735) によって、トランザクション不要なメソッドを他のクラスの公開メソッドにすることもできます

* 更新されたデータの読み込みは、同一のトランザクション内で起こなわれなければなりません
    * シミュレーションやテストが、並列に実行されるときは問題になります

* ORM を使っている場合は、データベースへの変更を ORM に書き込ませなければなりません
    * *EntityManager.flush* とか (EJB 3.0)
    * データベースを直接読むオブジェクトがいるから

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

### Example: Object Transaction Rollback Teardown
* setUp メソッドでフィクスチャーの生成をする前に *beginTransaction* を呼ぶようにします
* teardown コードを *abortTransaction* の呼び出しに変更しました
* *Creation Method* (pp.415) を使っているなら、トランザクションをコミットしないように修正しましょう
    * トランザクション不要バージョンの facade メソッドを呼んでいます
    * *Poor Mans Humble Object* の一例になります
    * *getFlighsFromAirport* などのメソッドがトランザクションを開始したり終了したりするなら、同じように修正しないといけません

    public void testGetFlightsByOrigin_NoInboundFlight_TRBTD()
        throws Exception {
        // Fixture Setup
        TransactionManager.beginTransaction();
        BigDecimal outboundAirport = createTestAirPort("10F");
        BigDecimal inboundAirport = null;
        FlightDto expectedFlightDto = null;
        try {
            inboundAirport = createTestAirPort("11F");
            expectedFlightDto = createTestFlight(outboundAirport, inboundAirport);
            // Exercise System
            List flightsAtDestination1 = facade.getFlightsByOriginAirport(inboundAirport);
            // Verify Outcome
            assertEquals(0, flightsAtDestination1.size());
        } finally {
            TransactionManager.abortTransaction();
        }
    }
    
    private BigDecimal createTestAirPort(String airPortName)
        throws FlightBookingException {
        BigDecimal newAirportId =
              facade._createAirport(airportName,
                                    " Airport" + airportName,
                                    "City" + airportName);
       return newAirportId;
    }
    
    public class Facade {
    
        public BigDecimal createAirport(String airportCode,
                                        String name,
                                        String nearbyCity)
                 throws FlightBookingException {
            TransactionManager.beginTransaction();
            BigDecimal airportId = _createAirport(airportCode, name, nearbyCity);
            TransactionManager.commitTransaction();
            return airportId;
        }
        
        // private, nontransactional version for use by tests
        BigDecimal _createAirport(String airportCode,
                                  String name,
                                  String nearbyCity)
                 throws FlightBookingException, InavlidArgumentException {
            Airport airport = dataAccess.createAirport(airportCode,name, nearbyCity);
            logMessage("CreateFlight", airport.getCode());
            rerutnr airport.getId();
        }
    
    }

### Example: Database Transaction Rollback Teardown

*Transaction Script*[PEAA] として知られているパターンです。

* *Implicit Setup* (pp.424) でコネクションを確立し、トランザクションを開始します
* *Implicit Teardown* (pp.516) でトランザクションのロールバックとコネクションの切断を行います
* *NUnit* なのでインスタンス変数には null を代入します (テストオブジェクトが使い回されるから)

     [TestFixture]
     public class TransactionRollbackTearDownTest
     {
         private SqlConnection _Connection;
         private SqlTransaction _Transaction;
         
         public TransactionRollbackTearDownTest()
         {
         }
         
         [SetUp]
         public void Setup()
         {
             string dbConnectionString = ConfigurationSettings.AppSettings.Get("DbConnectionString");
             _Connection = new SqlConnection(dbConnectionString);
             _Connection.Open();
             _Transaction = _Connection.BeginTransaction();
         }
         
         [TearDow]
         public void TearDown()
         {
             _Transaction.Rollback();
             _Connection.CLose();
             // Avoid NUnit "instance behavior" bug
             _Transaction = null;
             _Connection = null;
         }
         
         [Test]
         public void AnNUnitTest()
         {
             const string C_INSERT_SQL =
                 "INSERT INTO Invoice(Amount, Tax, CustomerId)" +
                 " Values({0}, {1}, {2})";
             SqlCommand cmd = _Connection.CreateCommand();
             cmd.Transaction = _Transaction;
             cmd.CommandText = string.Format(
                 C_INSERT_SQL,
                 new object[]{"100.00", "7.00", 2001});
             // Exercise SUT
             cmd.ExecuteNonQuery();
             // Verify result
             // etc.
         }
     }
