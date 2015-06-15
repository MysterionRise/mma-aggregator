index=1;
for name in *.jpg
do
    mv "${name}" "${index}.jpg"
    index=$((index+1))
done
