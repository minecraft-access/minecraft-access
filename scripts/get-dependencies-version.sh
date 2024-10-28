if [ "$#" -ne 1 ]; then
  echo "Usage: $0 <minecraft_version>"
  echo "Example: $0 1.21.3"
  exit 1
fi

MC_V=$1
MC_V_FOR_NEOFORGE=$(echo "${MC_V}" | cut -d'.' -f2,3)
FABRIC_LOADER_V=$(curl https://meta.fabricmc.net/v2/versions/loader | grep -B1 ": true" | grep -Po '(?<="version":\s").*(?=",)')
FABRIC_YARN_V=$(curl https://meta.fabricmc.net/v2/versions/yarn | grep -Po "(?<=net.fabricmc:yarn:)${MC_V}.*(?=\",)" | head -n1)
FABRIC_API_V=$(curl https://maven.fabricmc.net/net/fabricmc/fabric-api/fabric-api/maven-metadata.xml | grep -Po "(?<=<version>).*${MC_V}.*(?=</version>)" | tail -n1)
NEOFORGE_V=$(curl https://maven.neoforged.net/releases/net/neoforged/neoforge/maven-metadata.xml | grep -Po "(?<=<version>).*${MC_V_FOR_NEOFORGE}.*(?=</version>)" | tail -n1)
TEMP_FILE=$(curl "https://maven.fabricmc.net/net/fabricmc/fabric-api/fabric-api/${FABRIC_API_V}/fabric-api-${FABRIC_API_V}.pom")

get_version_from_pom() {
  echo "$1" | grep -A1 -P "<artifactId>$2</artifactId>" | grep -Po "(?<=<version>).*(?=</version>)"
}

FABRIC_RESOURCE_LOADER_V=$(get_version_from_pom "${TEMP_FILE}" "fabric-resource-loader-v0")
FABRIC_KEY_BINDING_API_V=$(get_version_from_pom "${TEMP_FILE}" "fabric-key-binding-api-v1")

echo "- Result -"
echo "(Minecraft: ${MC_V})"
echo "fabric-loader: ${FABRIC_LOADER_V}"
echo "fabric yarn: ${FABRIC_YARN_V}"
echo "(fabric-api: ${FABRIC_API_V})"
echo "fabric-resource-loader-v0: ${FABRIC_RESOURCE_LOADER_V}"
echo "fabric-key-binding-api-v1: ${FABRIC_KEY_BINDING_API_V}"
echo "neoforge: ${NEOFORGE_V}"
echo ""
echo "- Markdown text -"
echo "Compatibility For ${MC_V}
---------------------------

* Minecraft: ${MC_V} ([read setup guide](/docs/setup/basic.md))
* Fabric Loader: >=${FABRIC_LOADER_V} ([download Fabric Loader](https://fabricmc.net/use/installer/))
* NeoForge: ${NEOFORGE_V} ([download NeoForge installer](https://maven.neoforged.net/releases/net/neoforged/neoforge/${NEOFORGE_V}/neoforge-${NEOFORGE_V}-installer.jar))
* Java: >=21 ([download x64 Windows installer](https://download.oracle.com/java/21/latest/jdk-21_windows-x64_bin.msi), [download page for all Operating Systems](https://www.oracle.com/java/technologies/downloads/#java21))
"