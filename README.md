# Introduction

This project is a backend-only authentication server implemented in Java. The frontend interface is located [here](https://github.com/anthony-poon/ts.authentication-interface).
It is intended as a technical demonstration of advanced authentication design. 

Key features include:
- Two-Factor Authentication (2FA) with TOTP.
- Multi-site / multi-application login support (i.e. multiple client apps share this central server)
- JWT issuance & validation
- Refresh token mechanism to renew expired tokens
- Secure password storage (hashing / salting)
- Token revocation / blacklist support