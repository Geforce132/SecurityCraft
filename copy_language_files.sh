#!/bin/bash
cd src/main/resources/assets/securitycraft/lang

declare -A languages=(
	["de"]="de_at de_ch bar ksh nds_de"
	["es"]="es_ar es_cl es_ec es_mx es_uy es_ve"
	["fr"]="fr_ca"
)

for standard in "${!languages[@]}"; do
	declare -a dialects=(${languages[$standard]})

	for dialect in "${dialects[@]}"; do
		cp "${standard}_${standard}.json" "${dialect}.json"
	done
done