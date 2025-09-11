import { useState } from 'react'
import {
  AppLayout,
  TopNavigation,
  SideNavigation,
  BreadcrumbGroup,
  Button,
  SpaceBetween,
  Header,
  Container,
  Grid,
  Box,
  Table,
  ColumnLayout,
  TextContent,
  Badge,
  Icon
} from '@cloudscape-design/components'
import './App.css'

function App() {
  const [navigationOpen, setNavigationOpen] = useState(false)

  // Sample data for the dashboard
  const metrics = [
    { title: 'Total Events Conducted', value: '24', trend: '+12%' },
    { title: 'Total Tickets Sold', value: '1,847', trend: '+8%' },
    { title: 'Revenue to Date', value: '$45,230', trend: '+15%' }
  ]

  // Sample events data
  const events = [
    {
      id: '1',
      name: 'Tech Conference 2024',
      date: '2024-03-15',
      venue: 'Convention Center',
      ticketsSold: 450,
      status: 'Active'
    },
    {
      id: '2',
      name: 'Music Festival',
      date: '2024-04-20',
      venue: 'Central Park',
      ticketsSold: 1200,
      status: 'Active'
    },
    {
      id: '3',
      name: 'Business Workshop',
      date: '2024-05-10',
      venue: 'Business Center',
      ticketsSold: 85,
      status: 'Active'
    }
  ]

  const navigationItems = [
    {
      type: 'section',
      text: 'Dashboard',
      items: [
        { type: 'link', text: 'Overview', href: '#/dashboard' },
        { type: 'link', text: 'Analytics', href: '#/analytics' }
      ]
    },
    {
      type: 'section',
      text: 'Events',
      items: [
        { type: 'link', text: 'All Events', href: '#/events' },
        { type: 'link', text: 'Create Event', href: '#/events/create' },
        { type: 'link', text: 'Event Categories', href: '#/events/categories' }
      ]
    },
    {
      type: 'section',
      text: 'Tickets',
      items: [
        { type: 'link', text: 'Ticket Sales', href: '#/tickets' },
        { type: 'link', text: 'Refunds', href: '#/tickets/refunds' }
      ]
    },
    {
      type: 'section',
      text: 'Settings',
      items: [
        { type: 'link', text: 'Account', href: '#/settings/account' },
        { type: 'link', text: 'Notifications', href: '#/settings/notifications' }
      ]
    }
  ]

  const breadcrumbItems = [
    { text: 'Dashboard', href: '#/dashboard' },
    { text: 'Events', href: '#/events' }
  ]

  return (
    <div style={{ display: 'flex', flexDirection: 'column', height: '100vh' }}>
      {/* Top Navigation */}
      <TopNavigation
        identity={{
          href: '#/',
          title: 'Conductor',
          logo: { src: '/vite.svg', alt: 'Conductor' }
        }}
        i18nStrings={{
          searchIconAriaLabel: 'Search',
          searchDismissIconAriaLabel: 'Close search',
          overflowMenuTriggerText: 'More',
          overflowMenuBackIconAriaLabel: 'Back',
          overflowMenuDismissIconAriaLabel: 'Close menu'
        }}
        search={
          <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
            <Button
              iconName="menu"
              variant="icon"
              onClick={() => setNavigationOpen(!navigationOpen)}
              ariaLabel="Toggle Navigation"
            />
          </div>
        }
        utilities={[
          {
            type: 'button',
            iconName: 'settings',
            title: 'Settings',
            ariaLabel: 'Settings'
          },
          {
            type: 'menu-dropdown',
            text: 'User',
            iconName: 'user-profile',
            items: [
              { id: 'profile', text: 'Profile' },
              { id: 'preferences', text: 'Preferences' },
              { id: 'signout', text: 'Sign out' }
            ]
          }
        ]}
      />

      {/* Main Content Area */}
      <div style={{ display: 'flex', flex: 1, overflow: 'hidden' }}>
        {/* Side Navigation */}
        <div style={{ 
          width: navigationOpen ? '280px' : '0px', 
          transition: 'width 0.3s ease',
          overflow: 'hidden',
          borderRight: '1px solid #d1d5db'
        }}>
          <SideNavigation
            header={{ href: '#/', text: 'Conductor' }}
            items={navigationItems}
          />
        </div>

        {/* Content Area */}
        <div style={{ flex: 1, overflow: 'auto', padding: '20px' }}>
          <SpaceBetween direction="vertical" size="l">
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <div>
                <Header variant="h1">Events</Header>
                <BreadcrumbGroup items={breadcrumbItems} />
              </div>
              <Button variant="primary">Create Event</Button>
            </div>

            {/* Service Overview Dashboard */}
            <Container header={<Header variant="h2">Service Overview</Header>}>
              <div style={{ display: 'flex', alignItems: 'center' }}>
                {metrics.map((metric, index) => (
                  <div key={index} style={{ flex: 1, textAlign: 'center', position: 'relative' }}>
                    <Box textAlign="center">
                      <TextContent>
                        <div style={{ fontSize: '2rem', fontWeight: 'bold', color: '#0073bb' }}>
                          {metric.value}
                        </div>
                        <div style={{ fontSize: '1rem', color: '#666' }}>
                          {metric.title}
                        </div>
                        <div style={{ fontSize: '0.875rem', color: '#0073bb' }}>
                          {metric.trend}
                        </div>
                      </TextContent>
                    </Box>
                    {index < metrics.length - 1 && (
                      <div style={{ 
                        position: 'absolute',
                        right: '0',
                        top: '50%',
                        transform: 'translateY(-50%)',
                        width: '1px', 
                        height: '60px', 
                        backgroundColor: '#d1d5db'
                      }} />
                    )}
                  </div>
                ))}
              </div>
            </Container>

            {/* Active Events Table */}
            <Container header={<Header variant="h2">Active Events</Header>}>
              <Table
                columnDefinitions={[
                  {
                    id: 'name',
                    header: 'Event Name',
                    cell: item => item.name
                  },
                  {
                    id: 'date',
                    header: 'Date',
                    cell: item => item.date
                  },
                  {
                    id: 'venue',
                    header: 'Venue',
                    cell: item => item.venue
                  },
                  {
                    id: 'ticketsSold',
                    header: 'Tickets Sold',
                    cell: item => item.ticketsSold.toLocaleString()
                  },
                  {
                    id: 'status',
                    header: 'Status',
                    cell: item => <Badge color="green">{item.status}</Badge>
                  }
                ]}
                items={events}
                empty="No events found"
              />
            </Container>
          </SpaceBetween>
        </div>
      </div>

      {/* Footer */}
      <div style={{ 
        padding: '16px', 
        textAlign: 'center', 
        borderTop: '1px solid #d1d5db',
        backgroundColor: '#f9f9f9'
      }}>
        <TextContent>
          <div style={{ fontSize: '0.875rem', color: '#6b7280' }}>
            © 2024 Conductor. All rights reserved.
          </div>
        </TextContent>
      </div>
    </div>
  )
}

export default App
