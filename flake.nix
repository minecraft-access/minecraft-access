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
                        jdk25
                        libpulseaudio
                        libGL
                        openal
                        speechd
                        libx11
                    ];
                    nativeBuildInputs = with pkgs.buildPackages; [
                        jdk25
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
