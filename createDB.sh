#!/bin/bash
if [ -d SchliessfachDB ]; then
  echo "Das Datenbankverzeichnis existiert bereits.";
  exit 1;
fi

./ij.sh <<EOF
connect 'jdbc:derby:SchliessfachDB;create=true';
run 'SchliessfachDB.sql';
exit;
EOF
