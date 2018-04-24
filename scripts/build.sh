set -e
for i in *.tex; do 
    pdflatex -interaction=nonstopmode -halt-on-error $i
done