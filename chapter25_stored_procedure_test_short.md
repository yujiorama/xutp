Stored Procedure Test
=====================

データベースの中に置かれるストアドプロシージャについて、テスト自動化のプラクティスを適用します

How It Works
------------

* アプリケーションとは独立した、ストアドプロシージャのためのユニットテスト
    * レイヤー交差テスト、回帰テストになります
    * テスト対象のストアドプロシージャの性質によって変わります

When to Use it
--------------

* 重要なロジックをストアドプロシージャに置いてるなら、テストを書くべきです
    * アプリケーションとは独立して検証できるようにします

* ストアドプロシージャテストが重要になるケース
    * 複数のアプリケーションから利用されるような場合
    * 他のチームによって開発されてる場合
    * アプリケーションテストによって適切にテストされることが保証できない場合

Implementation Notes
--------------------

1. ストアドプロシージャと同じ言語でテストを書いて、データベース側で実行する
2. アプリケーションの言語でテストを書いて、*Remote Proxy*[GOF] でストアドプロシージャにアクセスする

* ストアドプロシージャの開発担当者は (1) の方法でユニットテストを書くでしょう
* アプリケーションの開発担当は (2) の方法で受け入れテストを書くでしょう

### Variation: In-Database Stored Procedure Test

* テスト自動化に xUnit を使う利点は、テスト対象のコードと同じ言語でテストを書けることです
    * 新たな言語やデバッガの学習をしなくてもよいので、開発者に自動テストを学んでもらいやすくなります
    * ストアドプロシージャの担当になったチームには魅力的な方法でしょう
    * テストコードから実装の詳細へアクセスしやすいという利点もあります
    * カプセル化を破るので、*Overspecified Software* (pp.239) になってしまうかもしれません

* データベースソフトウェアの中には、いくつかのプログラミング言語をサポートしているものもあります
    * Oracle は PL/SQL と Java の両方をサポートしています
        * PL/SQL のストアドプロシージャをテストするために JUnit が使えます
    * MS SQL Server は C# をサポートしています
        * Transact-SQL のストアドプロシージャをテストするために NUnit が使えます

* ストアドプロシージャが、アプリケーションコードとは異なるリポジトリに保存されている場合も、このパターンは有効かもしれません
    * テストコードを SUT と同じリポジトリに置くことができるからです

### Variation: Remoted Stored Procedure Test

* ストアドプロシージャを "ブラックボックス" としたコンポーネントテストになります
    * *Remote Proxy* [GOF] を使って、詳細を隠蔽します
    * プロキシは *Service Facade* [CJ2EEP] や *Command* [GOF] として構成されます
    * Java の *JdbcOdbcCallableStatement* は *Command* に相当します

* 回帰テストにするのがお勧めです
    * フィクスチャーの生成や結果の検証が困難であれば、他のストアドプロシージャを使います
    * 他に必要なタスクがあればそれも実行します
    * xUnit ファミリーには、そういった機能を持たせるための拡張版があります
        * Java なら DbUnit
        * .NET なら NDbUnit

* 他のチームの開発者が欠陥無しのコードを書いてくれるとは信じられない時もこのパターンを適用します
    * たぶん彼らは *In-Database Stored Procedure Tests* も書いていません
    * 受け入れテストとして使えるパターンです
    * 実際の例をコラムに書きました

[コラム](chapter25_stored_procedure_test_column.txt)

* 欠点はテストの実行時間
    * データの準備に時間がかかる = *Slow Test*(pp.253) 
    * テストを *Subset Suite* (pp.592) に分割すれば、だいぶ速くなります

* アプリケーションの言語で書かれたすでにユニットテストの存在するロジックを、データベースに移植するときも役立ちます
    * お金と時間の節約
    * 書き直しで生じる変換エラーも防ぐことができます

Motivating Example
------------------

[サンプル](http://gist.github.com/737968)

Refactoring Notes
-----------------

これからテストコードを紹介するのでリファクタリングはありません...

### Example: In-Database Stored Procedure Test

[utPLSQL](http://utplsql.sourceforge.net/) のサンプルコードです。

    CREATE OR REPLACE PACKAGE BODY ut_calc_secs_between
    IS
        PROCEDURE ut_setup
        IS
        BEGIN
            NULL;
        END;
        PROCEDURE ut_teardown
        IS
        BEGIN
            NULL;
        END;
        -- For each program to test...
        PROCEDURE ut_CALC_SECS_BETWEEN
        IS
            secs PLS_INTEGER;
        BEGIN
            CALC_SECS_BETWEEN (
                DATE1 => SYSDATE
                '
                DATE2 => SYSDATE
                '
                SECS => secs
            );
    
            utAssert.eq (
                'Same dates',
                secs,
                0
                );
        END ut_CALC_SECS_BETWEEN;
    
    END ut_calc_secs_between;
    /

### Example: Remoted Stored Procedure Test

    public class StoredProcedureTest extends TestCase {
        public void testCalcSecsBetween_SameTime() {
            // Setup
            TimeCalculatorProxy SUT = new TimeCalculatorProxy();
            Calendar cal = new GregorianCalendar();
            long now = cal.getTimeInMillis();
            // Exercise
            long timeDifference = SUT.calc_secs_between(now,now);
            // Verify
            assertEquals( 0, timeDifference );
        }
    }
