# application-external-context

![Version: 0.1.0](https://img.shields.io/badge/Version-0.1.0-informational?style=flat-square) ![Type: application](https://img.shields.io/badge/Type-application-informational?style=flat-square) ![AppVersion: 0.1.0](https://img.shields.io/badge/AppVersion-0.1.0-informational?style=flat-square)

A Helm chart for Kubernetes

**Homepage:** <https://github.com/epmd-edp/java-maven-java17.git>

## Maintainers

| Name | Email | Url |
| ---- | ------ | --- |
| DEV Team |  |  |

## Source Code

* <https://github.com/epmd-edp/java-maven-java17.git>

## Values

| Key | Type | Default | Description |
|-----|------|---------|-------------|
| affinity | object | `{}` |  |
| api.basePath | string | `"/v1alpha/applications"` |  |
| automountServiceAccountToken | bool | `true` |  |
| clusterName | string | `""` |  |
| container.extraEnvVars | string | `""` |  |
| container.extraVolumeMounts | string | `""` |  |
| container.extraVolumes | string | `""` |  |
| container.livenessProbe | string | `"httpGet:\n  path: /actuator/health/liveness\n  port: {{ .Values.container.port }}\n  httpHeaders:\n    - name: X-B3-Sampled\n      value: \"0\"\nfailureThreshold: 1\ninitialDelaySeconds: 180\nperiodSeconds: 20\nsuccessThreshold: 1\ntimeoutSeconds: 5\n"` |  |
| container.port | int | `8080` |  |
| container.readinessProbe | string | `"httpGet:\n  path: /actuator/health/readiness\n  port: {{ .Values.container.port }}\n  httpHeaders:\n    - name: X-B3-Sampled\n      value: \"0\"\nfailureThreshold: 30\ninitialDelaySeconds: 30\nperiodSeconds: 10\nsuccessThreshold: 1\ntimeoutSeconds: 1\n"` |  |
| container.resources.limits | object | `{}` |  |
| container.resources.requests | object | `{}` |  |
| container.securityContext.runAsUser | int | `1001` |  |
| container.startupProbe | string | `""` |  |
| dbchecker.enabled | bool | `true` |  |
| dbchecker.image.pullPolicy | string | `"IfNotPresent"` |  |
| dbchecker.image.repository | string | `"busybox"` |  |
| dbchecker.image.tag | float | `1.32` |  |
| dbchecker.securityContext.allowPrivilegeEscalation | bool | `false` |  |
| dbchecker.securityContext.runAsGroup | int | `1000` |  |
| dbchecker.securityContext.runAsNonRoot | bool | `true` |  |
| dbchecker.securityContext.runAsUser | int | `1000` |  |
| dnsPolicy | string | `"ClusterFirst"` |  |
| dnsWildcard | string | `""` |  |
| externalContext.ttl | string | `"30d"` |  |
| extraInitContainers | string | `""` |  |
| extraTrafficExcludeOutboundPorts | string | `"6379,26379,26380,27017"` |  |
| fullnameOverride | string | `""` |  |
| global.deploymentMode | string | `nil` |  |
| global.deploymentStrategy | string | `"Recreate"` |  |
| global.imagePullSecrets | list | `[]` |  |
| global.imageRegistry | string | `""` |  |
| image.pullPolicy | string | `"IfNotPresent"` |  |
| image.pullSecrets[0] | string | `"regcred"` |  |
| image.repository | string | `"application-external-context"` |  |
| image.tag | string | `"latest"` |  |
| ingress.enabled | bool | `true` |  |
| ingress.ingressClassName | string | `""` |  |
| istio.sidecar.enabled | bool | `true` |  |
| istio.sidecar.requestsLimitsEnabled | bool | `true` |  |
| istio.sidecar.resources.limits | object | `{}` |  |
| istio.sidecar.resources.requests | object | `{}` |  |
| keycloak.certificatesEndpoint | string | `"/protocol/openid-connect/certs"` |  |
| keycloak.realms.admin | string | `"admin"` |  |
| keycloak.realms.mygov | string | `"mygov-biz-portal"` |  |
| keycloak.url | string | `""` |  |
| lifecycleHooks | object | `{}` |  |
| mongodb.appSecretName | string | `"mongodb-app-secrets"` |  |
| mongodb.autoIndexCreation | bool | `true` |  |
| mongodb.collections.applicationSteps | string | `"application_steps"` |  |
| mongodb.collections.applicationVars | string | `"application_vars"` |  |
| mongodb.database | string | `"application_external_context"` |  |
| mongodb.databaseOverride | string | `""` |  |
| mongodb.integrationSecretName | string | `"mongodb-connection-details"` |  |
| mongodb.replicaset.enabled | bool | `false` |  |
| mongodb.ssl.enabled | bool | `false` |  |
| nameOverride | string | `""` |  |
| nodeSelector | object | `{}` |  |
| platform.logging.aspect.enabled | bool | `false` |  |
| podAnnotations | object | `{}` |  |
| podSecurityContext.fsGroup | int | `1001` |  |
| replicas | int | `1` |  |
| schedulerName | string | `"default-scheduler"` |  |
| service.nodePort | string | `""` |  |
| service.port | int | `8080` |  |
| service.type | string | `"ClusterIP"` |  |
| serviceAccount.create | bool | `true` |  |
| serviceAccount.name | string | `"application-external-context"` |  |
| terminationGracePeriodSeconds | int | `30` |  |
| tolerations | list | `[]` |  |
