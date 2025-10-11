import { INavData } from '@coreui/angular';

export const navItems: INavData[] = [
  {
    name: 'Dashboard',
    url: '/dashboard',
    iconComponent: { name: 'cil-speedometer' },
   
  },
 
  {
    name: 'SITIOS',
    title: true
  },
  {
    name: 'San Miguel',
    url: '/base',
    iconComponent: { name: 'cil-home' },
    children: [
      {
        name: 'OLT-SM01',
        url: '/base/accordion',
        icon: 'nav-icon-bullet'
      },
      {
        name: 'OLT-SM02',
        url: '/base/breadcrumbs',
        icon: 'nav-icon-bullet'
      },
      {
        name: 'OLT-SM03',
        url: '/base/cards',
        icon: 'nav-icon-bullet'
      },
      {
        name: 'OLT-SM04',
        url: '/base/carousel',
        icon: 'nav-icon-bullet'
      },
      
    ]
  },
{
    name: 'Pilar',
    url: '/base',
    iconComponent: { name: 'cil-home' },
    children: [
      {
        name: 'OLT-PI01',
        url: '/base/accordion',
        icon: 'nav-icon-bullet'
      },
      {
        name: 'OLT-PI02',
        url: '/base/breadcrumbs',
        icon: 'nav-icon-bullet'
      },
      {
        name: 'OLT-PI03',
        url: '/base/cards',
        icon: 'nav-icon-bullet'
      },
      {
        name: 'OLT-PI04',
        url: '/base/carousel',
        icon: 'nav-icon-bullet'
      },
      
    ]
  },
{
    name: 'GESTION OLTS',
    title: true
  },
{
name: 'Alta',
    url: '/base/olt-registration',
    iconComponent: { name: 'cil-pencil' },
},
{
name: 'Baja',
    url: '/base',
    iconComponent: { name: 'cil-trash' },
},
{
    name: 'GESTION ONTs',
    title: true
  },
{
name: 'Buscar',
    url: '/base',
    iconComponent: { name: 'cil-pencil' },
},



 {
    title: true,
    name: 'Theme'
  },
  {
    name: 'Colors',
    url: '/theme/colors',
    iconComponent: { name: 'cil-drop' }
  },
  {
    name: 'Typography',
    url: '/theme/typography',
    linkProps: { fragment: 'headings' },
    iconComponent: { name: 'cil-pencil' }
  },

 
  {
    title: true,
    name: 'Links',
    class: 'mt-auto'
  },
  {
    name: 'Docs',
    url: 'https://coreui.io/angular/docs/',
    iconComponent: { name: 'cil-description' },
    attributes: { target: '_blank' }
  }
];
