{
    inputs = {
        nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
        flake-utils.url = "github:numtide/flake-utils";
    };

    outputs = { self, nixpkgs, flake-utils }:
        flake-utils.lib.eachDefaultSystem (system:
            let
                pkgs = import nixpkgs { inherit system; };
            in
            {
                devShells.default = pkgs.mkShell rec {
                    buildInputs = with pkgs; [
                        jetbrains.jdk
                        libpulseaudio
                        libGL
                        openal
                        speechd
                        libx11
                    ];
                    nativeBuildInputs = with pkgs.buildPackages; [
                        jetbrains.jdk
                        git
                        hugo
                        wlc
                    ];

                    LD_LIBRARY_PATH = pkgs.lib.makeLibraryPath buildInputs;
                    JAVA_HOME = pkgs.jdk25;
                };
            }
        );
}
