#!/bin/bash
# Convert to square legacy icons
convert icon.png -resize 48x48 app/src/main/res/mipmap-mdpi/ic_launcher.png
convert icon.png -resize 72x72 app/src/main/res/mipmap-hdpi/ic_launcher.png
convert icon.png -resize 96x96 app/src/main/res/mipmap-xhdpi/ic_launcher.png
convert icon.png -resize 144x144 app/src/main/res/mipmap-xxhdpi/ic_launcher.png
convert icon.png -resize 192x192 app/src/main/res/mipmap-xxxhdpi/ic_launcher.png

# Create a circular mask for round icons
convert -size 1024x1024 xc:black -fill white -draw "circle 512,512 512,0" mask.png
convert icon.png mask.png -alpha set -compose DstIn -composite icon_round.png

# Convert to round legacy icons
convert icon_round.png -resize 48x48 app/src/main/res/mipmap-mdpi/ic_launcher_round.png
convert icon_round.png -resize 72x72 app/src/main/res/mipmap-hdpi/ic_launcher_round.png
convert icon_round.png -resize 96x96 app/src/main/res/mipmap-xhdpi/ic_launcher_round.png
convert icon_round.png -resize 144x144 app/src/main/res/mipmap-xxhdpi/ic_launcher_round.png
convert icon_round.png -resize 192x192 app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png

# Delete the old .webp files
rm app/src/main/res/mipmap-*/ic_launcher.webp
rm app/src/main/res/mipmap-*/ic_launcher_round.webp
