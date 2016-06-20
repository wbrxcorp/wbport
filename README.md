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

jee/ 以下で sbt run すると REPLに入る。また、同時にWebインターフェイスも起動する。

## プロファイル

scalaパッケージ profiles 以下に各プロファイル名のサブパッケージがあり、起動時にコマンドライン引数で指定できる。省略時はsbtのプロジェクト名が採用される。プロジェクト名のプロファイルが存在しない場合は default が使用される。

## モジュール

scalaパッケージ modules以下に各モジュール名のサブパッケージがあり、直下のModuleオブジェクトがモジュール本体として扱われる。どのモジュールが読み込まれるかはプロファイルの ImportModule#modulesで決定される。

モジュールのinitにはREPL環境のの参照が渡されるので、ここでおせっかいなimportやimplicitの宣言をしておいたりする。
