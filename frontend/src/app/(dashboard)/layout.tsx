'use client';

import { DashboardHeader } from '@/components/layout/DashboardHeader';
import { AuthGuard } from '@/lib/auth';

export default function DashboardLayout({ children }: { children: React.ReactNode }) {
  return (
    <AuthGuard>
      <div className="min-h-screen bg-gray-50">
        <DashboardHeader />
        <main>{children}</main>
      </div>
    </AuthGuard>
  );
}
