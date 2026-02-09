clean:
	make -C app clean

build:
	make -C app build

test:
	make -C app test

report:
	make -C app report

sonar:
	make -C app sonar

lint:
	make -C app lint

run:
	make -C app run