#!/bin/bash

set -e

RAW_DIR="./raw"
OUTPUT_DIR="./output"
MAX_SIZE=$((10 * 1024 * 1024)) # 10MB
MANIFEST="$OUTPUT_DIR/manifest.json"
TAG="v1.0.0"
REPO="fergdev/hagah"

mkdir -p "$OUTPUT_DIR"
rm -f "$OUTPUT_DIR"/*

# Start manifest
echo '{' > "$MANIFEST"
echo '  "version": "'"$TAG"'",' >> "$MANIFEST"
echo '  "videos": [' >> "$MANIFEST"

first=true

# Process each video
for file in "$RAW_DIR"/*.mp4; do
  [ -e "$file" ] || continue
  filename=$(basename "$file")
  output_file="$OUTPUT_DIR/$filename"

  echo "Compressing $filename..."

  ffmpeg -loglevel error -i "$file" \
    -vf "scale=1280:-2" \
    -c:v libx264 \
    -preset slow \
    -crf 28 \
    -c:a aac \
    "$output_file"

  if [ ! -f "$output_file" ]; then
    echo "Compression failed: $output_file not found"
    exit 1
  fi

  size=$(stat -f%z "$output_file")

  if [ "$size" -gt "$MAX_SIZE" ]; then
    echo "ERROR: $filename exceeds 10MB ($(du -h "$output_file" | cut -f1)). Aborting..."
    rm "$output_file"
    exit 1
  fi

  url="https://github.com/$REPO/releases/download/$TAG/$filename"

  if [ "$first" = false ]; then
    echo ',' >> "$MANIFEST"
  fi
  first=false

  # Convert filename to title: "wild_ocean.mp4" → "Wild Ocean"
  title=$(basename "$filename" .mp4 | sed -E 's/_/ /g' | awk '{for(i=1;i<=NF;i++) $i=toupper(substr($i,1,1)) substr($i,2)}1')
  {
    echo '    {'
    echo '      "filename": "'"$filename"'",'
    echo '      "title": "'"$title"'",'
    echo '      "url": "'"$url"'",'
    echo '      "size": '"$size"
    echo '    }'
  } >> "$MANIFEST"
done

echo '  ]' >> "$MANIFEST"
echo '}' >> "$MANIFEST"

echo "Manifest written to $MANIFEST"

# Upload to GitHub Release
for file in "$OUTPUT_DIR"/*.mp4 "$MANIFEST"; do
  echo "Uploading $file..."
  gh release upload "$TAG" "$file" --clobber --repo "$REPO"
done

echo "Upload complete."