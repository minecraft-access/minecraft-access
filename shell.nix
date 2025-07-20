{ pkgs ? import <nixpkgs> {} }:

pkgs.mkShell rec {
    buildInputs = with pkgs; [
        jetbrains.jdk
        libpulseaudio
        libGL
        openal
        speechd
    ];
    LD_LIBRARY_PATH = pkgs.lib.makeLibraryPath buildInputs;
    nativeBuildInputs = with pkgs.buildPackages; [
        jetbrains.jdk
        git
        hugo
        wlc
    ];
}
