#!/bin/bash
cd src/main/resources/assets/securitycraft/lang

declare -A languages=(
	["de"]="de_at de_ch ksh_de nds_de"
	["es"]="es_ar es_cl es_mx es_uy es_ve"
	["fr"]="fr_ca"
)

for standard in "${!languages[@]}"; do
	declare -a dialects=(${languages[$standard]})

	for dialect in "${dialects[@]}"; do
		cp "${standard}_${standard}.lang" "${dialect}.lang"
	done
done