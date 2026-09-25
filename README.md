# How to run

## Windows (no git-bash) — use the .bat

```
java-17-upgrade-assessment-automation.bat C:\source\syndigo-sys ClientSecret 7418ee20731 2f0051B0aE verbose
```

With a corporate proxy in front of the Anypoint platform:

```
java-17-upgrade-assessment-automation.bat C:\source\syndigo-sys ClientSecret 7418ee20731 2f0051B0aE verbose ^
    --proxy-url http://proxy.corp.com:8080 --proxy-user alice --proxy-pass s3cret
```

## Linux/macOS (git-bash) — use the .sh

```
./java-17-upgrade-assessment-automation.sh /source/syndigo-sys ClientSecret 7418ee20731 2f0051B0aE verbose
```

## Arguments

Arg1 -> location of mule app (of parent folder of multiple apps)

Arg2 -> type of credentials UserPass or ClientSecret

Arg3 -> Mule UserName (or ClientId)

Arg4 -> Mule Password (or Secret)

Arg5 -> optional (verbose mode to see more loggings especially in java module)

## Proxy (.bat only)

The HTTP calls to the Anypoint platform can be routed through a corporate proxy.
Pass these optional named flags (in any order, after the positional args):

`--proxy-url`  -> host:port or http://host:port of the proxy (required to enable the proxy)

`--proxy-user` -> optional proxy username (basic auth)

`--proxy-pass` -> optional proxy password (basic auth)

When `--proxy-url` is not supplied the tool connects directly, as before. These
flags are forwarded to the JVM as the `proxy.url`, `proxy.user` and `proxy.pass`
system properties, which the tool reads to configure the HTTP client.

# java.17.upgrade.assessment.automation
read more here.
https://docs.google.com/document/d/1yEjjU8wrWYNjV7HoG_zxbtQT4LNunWBm9LYP6HFxYRo/edit#heading=h.ncm2rtuit5xw
