FROM microsoft/dotnet:2.0-runtime AS base
WORKDIR /app

FROM microsoft/dotnet:2.0-sdk AS build
WORKDIR /src
COPY *.sln ./
COPY BuzzStats.StoryIngester/BuzzStats.StoryIngester.csproj BuzzStats.StoryIngester/
RUN dotnet restore
COPY . .
WORKDIR /src/BuzzStats.StoryIngester
RUN dotnet build -c Release -o /app

FROM build AS publish
RUN dotnet publish -c Release -o /app

FROM base AS final
WORKDIR /app
COPY --from=publish /app .
ENTRYPOINT ["dotnet", "BuzzStats.StoryIngester.dll"]
