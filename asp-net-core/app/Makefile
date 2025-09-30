prepare:
	dotnet new web -n QrCode
	chmod a+rw -R QrCode
	dotnet new xunit -n QrCode.Tests
	chmod a+rw -R QrCode.Tests
	cd QrCode.Tests && dotnet add reference ../QrCode/QrCode.csproj && cd ..
	dotnet new sln -n QrCodeProject
	dotnet sln add QrCode/QrCode.csproj
	dotnet sln add QrCode.Tests/QrCode.Tests.csproj

install:
	dotnet build

dev: install
	dotnet watch run --project QrCode/QrCode.csproj

lint:
	dotnet format

test: lint
	dotnet test

.PHONY: test
