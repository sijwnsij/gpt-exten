# Artifact Cache

Reuse only when Name + Version + Platform + Architecture match and the Artifact still exists and has not expired. Artifact status and `expires_at` returned by GitHub are authoritative.

## Artifact Record: Black 25.9.0 Linux x64 CPython 3.13

- Artifact Name: black-25.9.0-linux-x64-cp313
- Artifact ID: 13960479540
- Run ID: 43256675267
- Workflow: .github/workflows/black-format-tools.yml
- File: black-25.9.0-linux-x64-cp313.tar.gz (inside the Artifact ZIP)
- Size: 2249081 bytes (Artifact ZIP)
- SHA256: 6c75670edb0d269610a00b4180191726a384e1ba1549cc2cd4cb032e7df5f7c8 (Artifact ZIP; matched GitHub digest and local file)
- Created At: 2026-11-12T16:15:38Z
- Expires At: 2027-02-10T16:15:30Z
- Storage Mode: SINGLE
- Original File: black-25.9.0-linux-x64-cp313.tar.gz
- Original Size: 2248207 bytes
- Original SHA256: 5f6f3a52a5225c2f1b1686d85914887c110db9dc7667e0bb6e629b8e498950a4
- Part Count: 1
- Part 1: Artifact ID 13960479540; Artifact Name black-25.9.0-linux-x64-cp313; Size 2249081; SHA256 6c75670edb0d269610a00b4180191726a384e1ba1549cc2cd4cb032e7df5f7c8
- Validation: Artifact expired=false at retrieval; archive and all wheel SHA256 values verified locally; offline installation and `python -m black --version` succeeded.
- Retention: requested 400 days; actual expiry above was returned by GitHub.

| Name | Version | Platform | Architecture | File/Path | SHA256 |
|---|---|---|---|---|---|
| black | 25.9.0 | Linux / CPython 3.13 / manylinux2014+ | x86_64 | formatter/wheels/black-25.9.0-cp313-cp313-manylinux_2_17_x86_64.manylinux2014_x86_64.manylinux_2_28_x86_64.whl | b756fc75871cb1bcac5499552d771822fd9db5a2bb8db2a7247936ca48f39831 |
| click | 8.5.1 | Python 3 / any | any | formatter/wheels/click-8.5.1-py3-none-any.whl | 8bb5376d2006950386f8c6427613334a2acadc5f559cb80e638d00b4df957e37 |
| mypy_extensions | 1.2.0 | Python 3 / any | any | formatter/wheels/mypy_extensions-1.2.0-py3-none-any.whl | 993e7a20aa5671eb41a7132cf555af30062da99729601601b9a8382b11cae768 |
| packaging | 26.3 | Python 3 / any | any | formatter/wheels/packaging-26.3-py3-none-any.whl | d7193f7c8e4e93f444fde0262bf90af30e16fa0ad0ad44cb553c87339b23cd1c |
| pathspec | 1.1.1 | Python 3 / any | any | formatter/wheels/pathspec-1.1.1-py3-none-any.whl | a00ce642f577bf7f473932318056212bc4f8bfdf53128c78bbd5af0b9b20b189 |
| platformdirs | 4.14.5 | Python 3 / any | any | formatter/wheels/platformdirs-4.14.5-py3-none-any.whl | 8e778c6fa421516f4d3369449f8df3b0e1b72c0e0005fe88e3ec772d0a14e750 |
| pytokens | 0.4.1 | Linux / CPython 3.13 / manylinux2014+ | x86_64 | formatter/wheels/pytokens-0.4.1-cp313-cp313-manylinux2014_x86_64.manylinux_2_17_x86_64.manylinux_2_28_x86_64.whl | 29d1d8fb1030af4d231789959f21821ab6325e463f0503a61d204343c9b355d1 |

## Artifact Record: ARM64 CTF reverse tools Linux x64 CPython 3.13

- Artifact Name: arm64-reverse-uc214-cs506-py313-linux-x64
- Artifact ID: 14229073554
- Run ID: 43875270521
- Workflow: .github/workflows/ctf-arm64-reverse-tools.yml
- File: arm64-reverse-unicorn-2.1.4-capstone-5.0.6-pyelftools-0.32-linux-x64-py313.tar.gz (inside the Artifact ZIP)
- Size: 17637988 bytes (Artifact ZIP)
- SHA256: 1a4751209224af8ef136ce8a48dddc854b9e60b74b86e1e3da96c2416104ed75 (Artifact ZIP; verified locally)
- Created At: 2026-11-16T13:23:29Z
- Expires At: 2027-02-14T13:23:20Z
- Storage Mode: SINGLE
- Original File: arm64-reverse-unicorn-2.1.4-capstone-5.0.6-pyelftools-0.32-linux-x64-py313.tar.gz
- Original Size: 17632327 bytes
- Original SHA256: b1d538f8624d9889dbe4ec62eaabbc2f71f62abb6523b6ff276f0419ab572c1e
- Part Count: 1
- Part 1: Artifact ID 14229073554; Artifact Name arm64-reverse-uc214-cs506-py313-linux-x64; Size 17637988; SHA256 1a4751209224af8ef136ce8a48dddc854b9e60b74b86e1e3da96c2416104ed75
- Validation: expired=false at retrieval; ZIP, tarball, wheel SHA256, offline install and imports verified.

| Name | Version | Platform | Architecture | File/Path | SHA256 |
|---|---|---|---|---|---|
| unicorn | 2.1.4 | Linux / CPython 3.13 / manylinux2014+ | x86_64 | reverse/wheels/unicorn-2.1.4-cp37-abi3-manylinux_2_17_x86_64.manylinux2014_x86_64.whl | 9d6e6dea140560de4ebd8446661f7ef84a357d428c14a3ef09dacd306ec8c239 |
| capstone | 5.0.6 | Linux / Python 3 / manylinux2014+ | x86_64 | reverse/wheels/capstone-5.0.6-py3-none-manylinux_2_17_x86_64.manylinux2014_x86_64.whl | 0358855773100bb91ae6681fabce7299df83156945ba943f6211061a592c54a6 |
| pyelftools | 0.32 | Python 3 / any | any | reverse/wheels/pyelftools-0.32-py3-none-any.whl | 013df952a006db5e138b1edf6d8a68ecc50630adbd0d83a2d41e7f846163d738 |

## Artifact Record: Android NDK r27c Linux x86_64 minimal toolchain, R8 8.3.37 and Noto Sans CJK (SystemPulse build)

- Artifact Name: systempulse-deps-r27c-part-000 .. systempulse-deps-r27c-part-003 (four independent GitHub artifacts)
- Artifact IDs: 14313840073, 14312324012, 14312982126, 14313480997 (in part order)
- Manifest Artifact Name: systempulse-deps-r27c-manifest
- Manifest Artifact ID: 14312378841
- Run ID: 44049764254
- Workflow: .github/workflows/systempulse-deps-r27c.yml
- File: systempulse-dependencies-r27c-linux-x64.tar.gz (split into four 90,000,000-byte maximum parts, ZIP-wrapped separately)
- Size: 293,850,770 bytes (original compressed tarball; zipped parts: 90,000,174 + 90,000,174 + 90,000,174 + 23,850,944 bytes)
- SHA256: 864c123a814b7c8580e912fbf462e04fdc28418547f645ece21c68a9fceb5492 (original compressed tarball, verified locally)
- Created At: 2026-11-17T08:48:48Z through 2026-11-17T08:48:58Z
- Expires At: 2027-02-15T08:47:39Z (actual GitHub expiration for all artifacts; expired=false at retrieval)
- Storage Mode: SPLIT
- Original File: systempulse-dependencies-r27c-linux-x64.tar.gz
- Original Size: 293850770 bytes
- Original SHA256: 864c123a814b7c8580e912fbf462e04fdc28418547f645ece21c68a9fceb5492
- Part Count: 4
- Part 000: Artifact ID 14313840073; Artifact Name systempulse-deps-r27c-part-000; Size 90000174 (ZIP); ZIP SHA256 602f2ee1dfa1b8b8238286b703bccb7ea9e55ff53d35090003f352557fcccbf8; raw part SHA256 d48a33c15c8c21d26fdef012348e6d2ab265bc9f71716ea9d8924a754ad703d5
- Part 001: Artifact ID 14312324012; Artifact Name systempulse-deps-r27c-part-001; Size 90000174 (ZIP); ZIP SHA256 c85616748c4cd68184e88703c219d4b951a9fd014d4587a04663ce21791c44ba; raw part SHA256 542bca9f79e6138b6778f0126ecc94af83ca9c302f8b6500ab0c9921e96d6b04
- Part 002: Artifact ID 14312982126; Artifact Name systempulse-deps-r27c-part-002; Size 90000174 (ZIP); ZIP SHA256 606c475aaafb75968d9bb9b1df6f8ca3a6acfa382a8b5712ea8ff2027e24831c; raw part SHA256 542f59f27b43723fac426311c04e537aceedded25b4466903a6583a9e875e438
- Part 003: Artifact ID 14313480997; Artifact Name systempulse-deps-r27c-part-003; Size 23850944 (ZIP); ZIP SHA256 68cc46c88a31b9ac72a2a418f8811c53d617d1ce1a9b91a851dd146e3a2e3645; raw part SHA256 c0db2125613a7657f1542d1de1d06f963e48070c3da167efbfa3df1e2eaa5e55
- Validation: all four ZIP hashes matched GitHub digests; each split part matched the manifest; reconstructed compressed tarball matched the independent sha256; the R8 JAR and font matched upstream pinned hashes. Contents are a reduced NDK r27c toolchain (clang 18.0.3, AArch64 sysroot), R8 JAR, and OFL Noto font.
- Retention: requested 400 days; actual GitHub expires_at corresponds to 90 days.

| Name | Version | Platform | Architecture | File/Path | SHA256 |
|---|---|---|---|---|---|
| Android NDK clang | r27c / 27.2.12479018 / clang 18.0.3 | Linux | x86_64 targeting Android ARM64 | android-ndk-r27c/toolchains/llvm/prebuilt/linux-x86_64/bin/clang | a871130d810536f7bb924c8aeaff57c66de27bed9b13d0ccafba25fdcc8bd02d |
| Android NDK lld | r27c / 27.2.12479018 | Linux | x86_64 targeting Android ARM64 | android-ndk-r27c/toolchains/llvm/prebuilt/linux-x86_64/bin/ld.lld | c4a7473d3b8a99a32335616838af2aa85c6333091abeab423be2a905ff5f288c |
| Android NDK llvm-strip | r27c / 27.2.12479018 | Linux | x86_64 targeting Android ARM64 | android-ndk-r27c/toolchains/llvm/prebuilt/linux-x86_64/bin/llvm-strip | 31a3cb87fbf7c040aa82e853458cc6dc6dd95526123fc33c88ee8116a0c91c32 |
| Android NDK sysroot | r27c / 27.2.12479018 | Linux target sysroot | aarch64-linux-android | android-ndk-r27c/toolchains/llvm/prebuilt/linux-x86_64/sysroot/ (part of tarball) | 864c123a814b7c8580e912fbf462e04fdc28418547f645ece21c68a9fceb5492 (container tarball) |
| R8 / D8 | 8.3.37 | Java | any | external/r8-8.3.37.jar | 59753e70a74f918389cc87f1b7d66b5c0862932559167425708ded159e3de439 |
| Noto Sans CJK SC Bold | Sans2.004 | font/OTF | any | external/NotoSansCJKsc-Bold.otf | b5f0d1a190a7f9b43c310a8850630af12553df32c4c050543f9059732d9b4c0a |
