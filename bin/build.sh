#!/usr/bin/env bash
set -euo pipefail

project_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

current_java_major="$(java -version 2>&1 | sed -nE '1s/.*version "([0-9]+).*/\1/p' || true)"

if [[ "$current_java_major" != "11" ]] && [[ "$(uname -s)" == "Darwin" ]] && [[ -x /usr/libexec/java_home ]]; then
  java_home="$(/usr/libexec/java_home -v 11 2>/dev/null || true)"
  if [[ -n "$java_home" ]]; then
    export JAVA_HOME="$java_home"
    export PATH="$JAVA_HOME/bin:$PATH"
  fi
fi

if ! command -v java >/dev/null 2>&1; then
  echo "Java 11 is required. On macOS: brew install openjdk@11" >&2
  exit 1
fi

java_major="$(java -version 2>&1 | sed -nE '1s/.*version "([0-9]+).*/\1/p')"
if [[ "$java_major" != "11" ]]; then
  echo "Java 11 is required by this legacy Play/sbt build; found Java ${java_major:-unknown}." >&2
  echo "On macOS: brew install openjdk@11" >&2
  exit 1
fi

cd "$project_dir"
exec sbt -batch "$@"
