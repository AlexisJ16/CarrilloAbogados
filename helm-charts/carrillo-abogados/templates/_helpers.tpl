{{/*
Expand the name of the chart.
*/}}
{{- define "carrillo-abogados.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create a default fully qualified app name.
*/}}
{{- define "carrillo-abogados.fullname" -}}
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
{{- define "carrillo-abogados.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Common labels
*/}}
{{- define "carrillo-abogados.labels" -}}
helm.sh/chart: {{ include "carrillo-abogados.chart" . }}
{{ include "carrillo-abogados.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
project: carrillo-legal-tech
{{- end }}

{{/*
Selector labels
*/}}
{{- define "carrillo-abogados.selectorLabels" -}}
app.kubernetes.io/name: {{ include "carrillo-abogados.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}