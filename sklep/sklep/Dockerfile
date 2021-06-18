from malgwinner/ebiznes as build
copy app ./app/
copy conf ./conf/
copy build.sbt ./
copy project ./project/

run ["sbt", "stage"]
expose 9000
cmd ["./target/universal/stage/bin/sklep"]
