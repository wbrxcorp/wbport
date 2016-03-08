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
