import type { NextRequest } from 'next/server';
import { NextResponse } from 'next/server';

// Public routes that don't require authentication
const publicRoutes = [
  '/',
  '/login',
  '/register',
  '/forgot-password',
  '/reset-password',
  '/terms',
  '/privacy',
  // Spanish routes (actual navigation)
  '/nosotros',
  '/servicios',
  '/equipo',
  '/contacto',
  '/blog',
  // English aliases (kept for compatibility)
  '/contact',
  '/about',
  '/services',
  '/team',
];

// Routes that authenticated users shouldn't access
const authRoutes = ['/login', '/register', '/forgot-password', '/reset-password'];

// Role-based route prefixes
const roleRoutes: Record<string, string[]> = {
  ROLE_ADMIN: ['/admin', '/dashboard', '/leads', '/clients', '/cases', '/calendar', '/settings'],
  ROLE_LAWYER: ['/dashboard', '/leads', '/clients', '/cases', '/calendar'],
  ROLE_CLIENT: ['/dashboard', '/my-cases', '/my-documents', '/my-appointments'],
};

export function middleware(request: NextRequest) {
  const { pathname } = request.nextUrl;

  // Get the access token from cookies or localStorage isn't accessible in middleware
  // We use cookies for server-side auth checking
  const accessToken = request.cookies.get('access_token')?.value;

  // Check if current route is public
  const isPublicRoute = publicRoutes.some(
    (route) => pathname === route || pathname.startsWith(`${route}/`)
  );

  // Check if current route is auth-only (login, register)
  const isAuthRoute = authRoutes.some(
    (route) => pathname === route || pathname.startsWith(`${route}/`)
  );

  // API routes should pass through
  if (pathname.startsWith('/api')) {
    return NextResponse.next();
  }

  // Static files and Next.js internals should pass through
  if (
    pathname.startsWith('/_next') ||
    pathname.startsWith('/static') ||
    pathname.includes('.') // Files with extensions
  ) {
    return NextResponse.next();
  }

  // If user is authenticated and trying to access auth routes, redirect to dashboard
  if (accessToken && isAuthRoute) {
    return NextResponse.redirect(new URL('/dashboard', request.url));
  }

  // If user is not authenticated and trying to access protected routes
  if (!accessToken && !isPublicRoute) {
    const loginUrl = new URL('/login', request.url);
    loginUrl.searchParams.set('redirect', pathname);
    return NextResponse.redirect(loginUrl);
  }

  // For role-based access control, we need to decode the token
  // However, JWT decoding in Edge Runtime requires a lightweight approach
  // For now, we'll do basic route protection and let client-side handle fine-grained access

  return NextResponse.next();
}

// Configure which paths the middleware should run on
export const config = {
  matcher: [
    /*
     * Match all request paths except:
     * - _next/static (static files)
     * - _next/image (image optimization files)
     * - favicon.ico (favicon file)
     * - public folder files
     */
    '/((?!_next/static|_next/image|favicon.ico|.*\\.(?:svg|png|jpg|jpeg|gif|webp)$).*)',
  ],
};
