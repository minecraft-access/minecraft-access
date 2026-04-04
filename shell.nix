{ pkgs ? import <nixpkgs> {} }:

pkgs.mkShell rec {
    buildInputs = with pkgs; [
        javaPackages.compiler.temurin-bin.jdk-25
        libpulseaudio
        libGL
        openal
        speechd
        libx11
    ];
    LD_LIBRARY_PATH = pkgs.lib.makeLibraryPath buildInputs;
    JAVA_HOME = pkgs.javaPackages.compiler.temurin-bin.jdk-25;
    nativeBuildInputs = with pkgs.buildPackages; [
        jetbrains.jdk
        git
        hugo
        wlc
    ];
}
