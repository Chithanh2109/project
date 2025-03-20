import { useState } from "react";
import { useQuery, useMutation } from "@tanstack/react-query";
import { format } from "date-fns";
import { vi } from "date-fns/locale";
import { 
  Card, 
  CardContent, 
  CardDescription, 
  CardFooter, 
  CardHeader, 
  CardTitle 
} from "@/components/ui/card";
import { 
  Dialog, 
  DialogContent, 
  DialogDescription, 
  DialogFooter, 
  DialogHeader, 
  DialogTitle, 
  DialogTrigger, 
  DialogClose
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Textarea } from "@/components/ui/textarea";
import { 
  CheckCircle2,
  Clock,
  LogIn,
  LogOut,
  UserCog,
  X,
  Search,
  Filter,
  Calendar,
  RefreshCcw
} from "lucide-react";
import { useToast } from "@/hooks/use-toast";
import { apiRequest, queryClient } from "@/lib/queryClient";
import { Appointment, User } from "@shared/schema";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Badge } from "@/components/ui/badge";

// Appointment status colors
const statusColors = {
  "chờ xác nhận": "bg-amber-100 text-amber-800",
  "đã xác nhận": "bg-blue-100 text-blue-800",
  "đang thực hiện": "bg-indigo-100 text-indigo-800",
  "hoàn thành": "bg-green-100 text-green-800",
  "đã hủy": "bg-red-100 text-red-800"
};

export function AppointmentManagement() {
  const { toast } = useToast();
  const [filter, setFilter] = useState<"all" | "today" | "week">("today");
  const [searchQuery, setSearchQuery] = useState("");
  const [selectedTherapist, setSelectedTherapist] = useState<number | null>(null);
  const [assignDialogOpen, setAssignDialogOpen] = useState(false);
  const [currentAppointment, setCurrentAppointment] = useState<Appointment | null>(null);
  const [resultNotes, setResultNotes] = useState("");

  // Fetch appointments
  const { data: appointments, isLoading: loadingAppointments } = useQuery<Appointment[]>({
    queryKey: ["/api/admin/appointments", filter],
    queryFn: async () => {
      const res = await apiRequest("GET", `/api/admin/appointments?timeframe=${filter}`);
      return res.json();
    }
  });

  // Fetch therapists
  const { data: therapists, isLoading: loadingTherapists } = useQuery<User[]>({
    queryKey: ["/api/admin/therapists"],
    queryFn: async () => {
      const res = await apiRequest("GET", "/api/admin/therapists");
      return res.json();
    }
  });

  // Appointment check-in mutation
  const checkinMutation = useMutation({
    mutationFn: async (appointmentId: number) => {
      const res = await apiRequest("POST", `/api/admin/appointments/${appointmentId}/checkin`);
      return res.json();
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["/api/admin/appointments"] });
      toast({
        title: "Checkin thành công",
        description: "Khách hàng đã được checkin.",
      });
    }
  });

  // Appointment check-out mutation
  const checkoutMutation = useMutation({
    mutationFn: async (data: { appointmentId: number, result: string }) => {
      const res = await apiRequest("POST", `/api/admin/appointments/${data.appointmentId}/checkout`, {
        result: data.result
      });
      return res.json();
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["/api/admin/appointments"] });
      toast({
        title: "Checkout thành công",
        description: "Dịch vụ đã hoàn thành.",
      });
    }
  });

  // Assign therapist mutation
  const assignTherapistMutation = useMutation({
    mutationFn: async (data: { appointmentId: number, therapistId: number }) => {
      const res = await apiRequest("POST", `/api/admin/appointments/${data.appointmentId}/assign`, {
        therapistId: data.therapistId
      });
      return res.json();
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["/api/admin/appointments"] });
      setAssignDialogOpen(false);
      toast({
        title: "Phân công thành công",
        description: "Đã phân công chuyên viên thực hiện dịch vụ.",
      });
    }
  });

  // Handle therapist assignment
  const handleAssignTherapist = () => {
    if (!currentAppointment || !selectedTherapist) return;
    
    assignTherapistMutation.mutate({
      appointmentId: currentAppointment.id,
      therapistId: selectedTherapist
    });
  };

  // Handle checkout and submit result
  const handleCheckout = (appointmentId: number) => {
    checkoutMutation.mutate({
      appointmentId,
      result: resultNotes
    });
    setResultNotes("");
  };

  // Filter appointments based on search query
  const filteredAppointments = appointments?.filter(appointment => {
    if (!searchQuery) return true;
    // This is a simple search, in a real app you'd search through customer names, etc.
    return appointment.id.toString().includes(searchQuery);
  });

  const formatDate = (date: string | Date) => {
    return format(new Date(date), "HH:mm - EEEE, dd/MM/yyyy", { locale: vi });
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row justify-between gap-4">
        <div className="flex items-center gap-2">
          <div className="relative">
            <Search className="absolute left-2.5 top-2.5 h-4 w-4 text-muted-foreground" />
            <Input
              placeholder="Tìm kiếm..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="pl-8 w-full sm:w-[300px]"
            />
          </div>
          <Button variant="outline" size="icon">
            <Filter className="h-4 w-4" />
            <span className="sr-only">Lọc</span>
          </Button>
        </div>

        <Tabs defaultValue="today" className="w-full sm:w-auto" onValueChange={(v) => setFilter(v as any)}>
          <TabsList>
            <TabsTrigger value="today">Hôm nay</TabsTrigger>
            <TabsTrigger value="week">Tuần này</TabsTrigger>
            <TabsTrigger value="all">Tất cả</TabsTrigger>
          </TabsList>
        </Tabs>
      </div>

      {loadingAppointments ? (
        <div className="flex justify-center py-12">
          <RefreshCcw className="h-8 w-8 animate-spin text-primary" />
        </div>
      ) : !filteredAppointments?.length ? (
        <Card>
          <CardContent className="py-10 text-center">
            <Calendar className="h-10 w-10 mx-auto text-muted-foreground mb-4" />
            <h3 className="text-lg font-medium">Không có lịch hẹn</h3>
            <p className="text-muted-foreground mt-1">
              Không tìm thấy lịch hẹn nào trong khoảng thời gian này.
            </p>
          </CardContent>
        </Card>
      ) : (
        <div className="grid gap-6">
          {filteredAppointments?.map((appointment) => (
            <Card key={appointment.id}>
              <CardHeader className="pb-3">
                <div className="flex justify-between items-start">
                  <div>
                    <CardTitle className="flex items-center">
                      Lịch hẹn #{appointment.id}
                      <Badge className={`ml-2 ${statusColors[appointment.status as keyof typeof statusColors] || "bg-gray-100"}`}>
                        {appointment.status}
                      </Badge>
                    </CardTitle>
                    <CardDescription className="mt-1">
                      <span className="flex items-center mt-1">
                        <Clock className="h-4 w-4 mr-1" />
                        {formatDate(appointment.date)}
                      </span>
                    </CardDescription>
                  </div>
                  <div className="flex gap-2">
                    {appointment.status === "đã xác nhận" && (
                      <Button 
                        size="sm" 
                        variant="secondary"
                        onClick={() => checkinMutation.mutate(appointment.id)}
                      >
                        <LogIn className="h-4 w-4 mr-1" />
                        Checkin
                      </Button>
                    )}
                    {appointment.status === "đang thực hiện" && (
                      <Dialog>
                        <DialogTrigger asChild>
                          <Button size="sm" variant="default">
                            <LogOut className="h-4 w-4 mr-1" />
                            Checkout
                          </Button>
                        </DialogTrigger>
                        <DialogContent>
                          <DialogHeader>
                            <DialogTitle>Hoàn thành dịch vụ</DialogTitle>
                            <DialogDescription>
                              Nhập kết quả thực hiện dịch vụ và checkout cho khách hàng
                            </DialogDescription>
                          </DialogHeader>
                          <div className="grid gap-4 py-4">
                            <div className="grid gap-2">
                              <Label htmlFor="result">Kết quả thực hiện</Label>
                              <Textarea
                                id="result"
                                placeholder="Nhập kết quả thực hiện dịch vụ..."
                                value={resultNotes}
                                onChange={(e) => setResultNotes(e.target.value)}
                                rows={5}
                              />
                            </div>
                          </div>
                          <DialogFooter>
                            <DialogClose asChild>
                              <Button variant="outline">Hủy</Button>
                            </DialogClose>
                            <Button onClick={() => handleCheckout(appointment.id)}>
                              <CheckCircle2 className="h-4 w-4 mr-1" />
                              Hoàn thành
                            </Button>
                          </DialogFooter>
                        </DialogContent>
                      </Dialog>
                    )}
                    {appointment.status === "chờ xác nhận" && !appointment.therapistId && (
                      <Dialog 
                        open={assignDialogOpen && currentAppointment?.id === appointment.id}
                        onOpenChange={(open) => {
                          setAssignDialogOpen(open);
                          if (open) setCurrentAppointment(appointment);
                          else setSelectedTherapist(null);
                        }}
                      >
                        <DialogTrigger asChild>
                          <Button size="sm" variant="outline">
                            <UserCog className="h-4 w-4 mr-1" />
                            Phân công
                          </Button>
                        </DialogTrigger>
                        <DialogContent>
                          <DialogHeader>
                            <DialogTitle>Phân công chuyên viên</DialogTitle>
                            <DialogDescription>
                              Chọn chuyên viên thực hiện dịch vụ cho lịch hẹn này
                            </DialogDescription>
                          </DialogHeader>
                          <div className="grid gap-4 py-4">
                            <div className="grid gap-2">
                              <Label htmlFor="therapist">Chuyên viên</Label>
                              {loadingTherapists ? (
                                <div className="flex justify-center py-2">
                                  <RefreshCcw className="h-5 w-5 animate-spin text-primary" />
                                </div>
                              ) : (
                                <Select 
                                  onValueChange={(value) => setSelectedTherapist(parseInt(value))}
                                >
                                  <SelectTrigger id="therapist">
                                    <SelectValue placeholder="Chọn chuyên viên" />
                                  </SelectTrigger>
                                  <SelectContent>
                                    {therapists?.map((therapist) => (
                                      <SelectItem key={therapist.id} value={therapist.id.toString()}>
                                        {therapist.fullName} - {therapist.specialization}
                                      </SelectItem>
                                    ))}
                                  </SelectContent>
                                </Select>
                              )}
                            </div>
                          </div>
                          <DialogFooter>
                            <DialogClose asChild>
                              <Button variant="outline">Hủy</Button>
                            </DialogClose>
                            <Button 
                              onClick={handleAssignTherapist}
                              disabled={!selectedTherapist}
                            >
                              Xác nhận
                            </Button>
                          </DialogFooter>
                        </DialogContent>
                      </Dialog>
                    )}
                  </div>
                </div>
              </CardHeader>
              <CardContent className="pb-4">
                <div className="grid md:grid-cols-2 gap-4">
                  <div>
                    <h4 className="text-sm font-semibold mb-1">Thông tin khách hàng</h4>
                    <p className="text-sm">ID: {appointment.customerId}</p>
                    <p className="text-sm">Mã lịch hẹn: #{appointment.id}</p>
                  </div>
                  <div>
                    <h4 className="text-sm font-semibold mb-1">Thông tin dịch vụ</h4>
                    <p className="text-sm">ID dịch vụ: {appointment.serviceId}</p>
                    {appointment.therapistId && (
                      <p className="text-sm">ID chuyên viên: {appointment.therapistId}</p>
                    )}
                  </div>
                </div>
                {appointment.notes && (
                  <div className="mt-4">
                    <h4 className="text-sm font-semibold mb-1">Ghi chú</h4>
                    <p className="text-sm text-muted-foreground">{appointment.notes}</p>
                  </div>
                )}
                {appointment.result && (
                  <div className="mt-4">
                    <h4 className="text-sm font-semibold mb-1">Kết quả thực hiện</h4>
                    <p className="text-sm text-muted-foreground">{appointment.result}</p>
                  </div>
                )}
              </CardContent>
              <CardFooter className="flex justify-between pt-0">
                <div className="text-sm text-muted-foreground">
                  {appointment.checkinTime && (
                    <span>Checkin: {format(new Date(appointment.checkinTime), "HH:mm")}</span>
                  )}
                  {appointment.checkoutTime && (
                    <span className="ml-4">Checkout: {format(new Date(appointment.checkoutTime), "HH:mm")}</span>
                  )}
                </div>
                {appointment.status === "đã xác nhận" && !appointment.therapistId && (
                  <Badge variant="outline" className="text-amber-500 border-amber-500">
                    Chưa phân công chuyên viên
                  </Badge>
                )}
              </CardFooter>
            </Card>
          ))}
        </div>
      )}
    </div>
  );
} 