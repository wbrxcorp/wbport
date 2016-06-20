# playground
Playground

## このパブリックリポジトリのプライベートなダウンストリームを作成する方法

空のプライベートリポジトリ (仮にprivとする) を GitHub上に作成し、次を行う

```
git clone git@github.com:wbrxcorp/priv.git
cd priv
git remote add upstream git@github.com:wbrxcorp/playground.git
git fetch upstream
git merge upstream/master
git push -u origin master
```

## アップストリームの変更を取り込むには

```
git fetch upstream
git merge upstream/master
```

## 起動

jee/ 以下で sbt run すると REPLに入る。また、同時にWebインターフェイスも起動する。REPLを抜けるには :quit とする。ターミナルがおかしくなっているので適宜resetすること。

## プロファイル

scalaパッケージ profiles 以下に各プロファイル名のサブパッケージがあり、起動時にコマンドライン引数で指定できる。省略時はsbtのプロジェクト名が採用される。プロジェクト名のプロファイルが存在しない場合は default が使用される。

## モジュール

scalaパッケージ modules以下に各モジュール名のサブパッケージがあり、直下のModuleオブジェクトがモジュール本体として扱われる。どのモジュールが読み込まれるかはプロファイルの ImportModule#modulesで決定される。

モジュールのinitにはREPL環境のの参照が渡されるので、ここでおせっかいなimportやimplicitの宣言をしておいたりする。

### databaseモジュール

プロファイルの DataSourceDefinition オブジェクトで指定されたデータベースへのアクセスを提供する。scalikejdbcの  sqlリテラルをREPL上から直接使用可能にする。AutoSessionが有効なのでDB接続への参照を取り回さなくて良い

```
scala> sql"select 1".map(_.int(1)).single.apply
```
