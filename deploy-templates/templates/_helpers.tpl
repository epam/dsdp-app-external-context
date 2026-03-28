{{/*
Expand the name of the chart.
*/}}
{{- define "application-external-context.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
If release name contains chart name it will be used as a full name.
*/}}
{{- define "application-external-context.fullname" -}}
{{- if .Values.fullnameOverride }}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- $name := default .Chart.Name .Values.nameOverride }}
{{- if contains $name .Release.Name }}
{{- .Release.Name | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" }}
{{- end }}
{{- end }}
{{- end }}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "application-external-context.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Common labels
*/}}
{{- define "application-external-context.labels" -}}
helm.sh/chart: {{ include "application-external-context.chart" . }}
{{ include "application-external-context.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{/*
Selector labels
*/}}
{{- define "application-external-context.selectorLabels" -}}
app.kubernetes.io/name: {{ include "application-external-context.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}

{{/*
Create the name of the service account to use
*/}}
{{- define "application-external-context.serviceAccountName" -}}
{{- if .Values.serviceAccount.create }}
{{- default (include "application-external-context.fullname" .) .Values.serviceAccount.name }}
{{- else }}
{{- default "default" .Values.serviceAccount.name }}
{{- end }}
{{- end }}

{{- define "keycloak.urlPrefix" }}
{{- printf "%s%s%s" .Values.keycloak.url "/realms/" .Release.Namespace }}
{{- end }}

{{- define "issuer.admin" -}}
{{- printf "%s-%s" (include "keycloak.urlPrefix" .) .Values.keycloak.realms.admin -}}
{{- end -}}

{{- define "issuer.mygov-biz" -}}
{{- printf "%s-%s" (include "keycloak.urlPrefix" .) .Values.keycloak.realms.mygov }}
{{- end }}

{{- define "jwksUri.admin" -}}
{{- printf "%s-%s%s" (include "keycloak.urlPrefix" .) .Values.keycloak.realms.admin .Values.keycloak.certificatesEndpoint -}}
{{- end -}}

{{- define "jwksUri.mygov-biz" -}}
{{- printf "%s-%s%s" (include "keycloak.urlPrefix" .) .Values.keycloak.realms.mygov .Values.keycloak.certificatesEndpoint }}
{{- end }}

{{- define "database.name" }}
{{- if .Values.mongodb.databaseOverride }}
{{- .Values.mongodb.databaseOverride }}
{{- else }}
{{- $clusterName := .Values.clusterName | replace "-" "_" }}
{{- $releaseNameSpace := .Release.Namespace | replace "-" "_" }}
{{- printf "%s_%s_%s" $clusterName $releaseNameSpace .Values.mongodb.database }}
{{- end }}
{{- end }}

{{- define "application-external-context.istioResources" }}
{{- if .Values.istio.sidecar.resources.limits.cpu }}
sidecar.istio.io/proxyCPULimit: {{ .Values.istio.sidecar.resources.limits.cpu | quote }}
{{- end }}
{{- if .Values.istio.sidecar.resources.limits.memory }}
sidecar.istio.io/proxyMemoryLimit: {{ .Values.istio.sidecar.resources.limits.memory | quote }}
{{- end }}
{{- if .Values.istio.sidecar.resources.requests.cpu }}
sidecar.istio.io/proxyCPU: {{ .Values.istio.sidecar.resources.requests.cpu | quote }}
{{- end }}
{{- if .Values.istio.sidecar.resources.requests.memory }}
sidecar.istio.io/proxyMemory: {{ .Values.istio.sidecar.resources.requests.memory | quote }}
{{- end }}
{{- end }}

