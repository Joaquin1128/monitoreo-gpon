# Monitoreo GPON Frontend

Frontend desarrollado en React con TypeScript y Material UI para el sistema de monitoreo GPON.

## Características

- **Dashboard moderno** con diseño responsivo usando Material UI v7
- **Autenticación JWT** con protección de rutas
- **Vista de dispositivos** organizados por Hubs con OLTs expandibles
- **Tablero de OLT** con métricas SNMP en tiempo real
- **Animaciones suaves** usando Framer Motion
- **API cliente** completo basado en OpenAPI specification

## Tecnologías

- React 19.2.0
- TypeScript 4.9.5
- Material UI v7
- Framer Motion v11
- Axios para HTTP requests
- React Router v6

## Estructura del Proyecto

```
src/
├── components/          # Componentes reutilizables
│   ├── Login.tsx       # Componente de autenticación
│   ├── RequireAuth.tsx # Wrapper de protección de rutas
│   ├── Sidebar.tsx     # Barra lateral principal
│   ├── OltSidebar.tsx  # Barra lateral para OLT seleccionada
│   └── MainLayout.tsx  # Layout principal
├── pages/              # Páginas de la aplicación
│   ├── DevicesView.tsx # Vista de dispositivos
│   └── OltDashboard.tsx # Tablero de OLT
├── services/           # Servicios API
│   ├── api.ts         # Cliente API principal
│   └── index.ts       # Servicios específicos
├── types/             # Definiciones de tipos TypeScript
│   └── api.ts         # Tipos basados en OpenAPI
└── api/              # Servicios legacy (mantener compatibilidad)
    ├── client.ts
    ├── token.ts
    └── ...
```

## Funcionalidades

### 1. Autenticación
- Login con JWT token
- Protección de rutas automática
- Logout con limpieza de token

### 2. Vista de Dispositivos
- Lista de Hubs con información de ubicación
- OLTs organizadas por Hub con estado visual
- Tabla detallada con información técnica
- Navegación directa a tablero de OLT

### 3. Tablero de OLT
- Información completa de la OLT seleccionada
- Datos del Hub padre
- Métricas SNMP en tiempo real
- Probing automático cada 10 segundos
- Probing manual bajo demanda

### 4. Barra Lateral Adicional
- Se muestra al seleccionar una OLT
- Información resumida de la OLT
- Navegación entre pestañas (Tablero)
- Diseño moderno con animaciones

## API Endpoints

El frontend se conecta con los siguientes endpoints del backend:

- `POST /api/auth/login` - Autenticación
- `GET /api/hubs` - Lista de hubs
- `GET /api/hubs/{id}` - Hub específico
- `GET /api/hubs/{id}/olts` - OLTs de un hub
- `GET /api/olts/{id}` - OLT específica
- `POST /api/snmp/olts/{id}/probe` - Probe SNMP de OLT

## Configuración

### Variables de Entorno
```bash
REACT_APP_API_URL=http://localhost:8080
```

### Instalación
```bash
npm install
npm start
```

## Diseño

- **Paleta de colores**: Tonos neutros con acentos azules y naranjas
- **Tipografía**: Roboto con pesos semibold para títulos
- **Componentes**: Cards con bordes redondeados, botones sin mayúsculas
- **Animaciones**: Transiciones suaves de 0.3-0.5s
- **Responsive**: Adaptable a diferentes tamaños de pantalla

## Estado de Desarrollo

✅ Completado:
- Cliente API completo
- Autenticación JWT
- Vista de dispositivos con Hubs y OLTs
- Tablero de OLT con métricas SNMP
- Barra lateral adicional
- Animaciones con Framer Motion
- Routing protegido
- Tema personalizado de Material UI

🔄 En desarrollo:
- Vista de ONTs asociadas a OLT
- Más métricas SNMP
- Gráficos de rendimiento
- Alertas y notificaciones

## Notas Técnicas

- El frontend está completamente tipado con TypeScript
- Los tipos están generados basándose en la especificación OpenAPI
- El sistema de probing usa un servicio singleton para evitar múltiples conexiones
- Las animaciones están optimizadas para rendimiento
- El diseño es completamente responsive