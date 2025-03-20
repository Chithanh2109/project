import { useState } from "react";
import { useLocation, Link } from "wouter";
import { Button } from "@/components/ui/button";
import { 
  LayoutDashboard, 
  Users, 
  Calendar, 
  Settings, 
  FileText, 
  ShoppingBag, 
  Star, 
  LogOut, 
  Menu,
  X,
  UserCog
} from "lucide-react";
import { useAuth } from "@/hooks/use-auth";
import { cn } from "@/lib/utils";

interface AdminLayoutProps {
  children: React.ReactNode;
}

export function AdminLayout({ children }: AdminLayoutProps) {
  const [location] = useLocation();
  const { user, signOut } = useAuth();
  const [sidebarOpen, setSidebarOpen] = useState(false);

  const navigation = [
    { name: 'Dashboard', href: '/admin', icon: LayoutDashboard },
    { name: 'Lịch Hẹn', href: '/admin/appointments', icon: Calendar },
    { name: 'Dịch Vụ', href: '/admin/services', icon: ShoppingBag },
    { name: 'Khách Hàng', href: '/admin/customers', icon: Users },
    { name: 'Chuyên Viên', href: '/admin/therapists', icon: UserCog },
    { name: 'Đánh Giá', href: '/admin/feedback', icon: Star },
    { name: 'Báo Cáo', href: '/admin/reports', icon: FileText },
    { name: 'Cài Đặt', href: '/admin/settings', icon: Settings },
  ];

  const toggleSidebar = () => setSidebarOpen(!sidebarOpen);

  return (
    <div className="flex h-screen bg-background">
      {/* Mobile sidebar toggle */}
      <div className="fixed top-4 left-4 z-40 md:hidden">
        <Button variant="outline" size="icon" onClick={toggleSidebar}>
          <Menu className="h-5 w-5" />
        </Button>
      </div>

      {/* Sidebar */}
      <div
        className={cn(
          "fixed inset-y-0 left-0 z-30 w-64 transform bg-card border-r transition-transform duration-300 ease-in-out md:translate-x-0",
          sidebarOpen ? "translate-x-0" : "-translate-x-full"
        )}
      >
        <div className="flex h-full flex-col">
          <div className="flex items-center justify-between px-4 py-5">
            <div className="flex items-center">
              <span className="text-xl font-semibold">SkinCareSync</span>
              <span className="ml-2 text-xs text-muted-foreground">Admin</span>
            </div>
            <Button variant="ghost" size="icon" onClick={toggleSidebar} className="md:hidden">
              <X className="h-5 w-5" />
            </Button>
          </div>

          <div className="flex-1 overflow-y-auto py-4">
            <nav className="space-y-1 px-2">
              {navigation.map((item) => {
                const isActive = location === item.href;
                return (
                  <Link key={item.name} href={item.href}>
                    <a
                      className={cn(
                        "flex items-center px-4 py-3 text-sm rounded-md transition-colors",
                        isActive
                          ? "bg-primary text-primary-foreground"
                          : "text-foreground hover:bg-muted"
                      )}
                    >
                      <item.icon className={cn("mr-3 h-5 w-5", isActive ? "text-primary-foreground" : "text-muted-foreground")} />
                      {item.name}
                    </a>
                  </Link>
                );
              })}
            </nav>
          </div>

          <div className="px-4 py-4 border-t">
            <div className="flex items-center">
              <div className="flex-shrink-0">
                <span className="inline-flex h-8 w-8 items-center justify-center rounded-full bg-primary text-primary-foreground">
                  {user?.fullName?.charAt(0) || user?.username?.charAt(0) || "U"}
                </span>
              </div>
              <div className="ml-3">
                <p className="text-sm font-medium">{user?.fullName || user?.username}</p>
                <p className="text-xs text-muted-foreground capitalize">{user?.role || "Admin"}</p>
              </div>
            </div>
            <Button variant="ghost" className="mt-4 w-full justify-start" onClick={signOut}>
              <LogOut className="mr-3 h-5 w-5" />
              Đăng xuất
            </Button>
          </div>
        </div>
      </div>

      {/* Main content */}
      <div className="flex flex-1 flex-col md:pl-64">
        <main className="flex-1 overflow-y-auto bg-muted/30 p-6">
          {children}
        </main>
      </div>
    </div>
  );
} 