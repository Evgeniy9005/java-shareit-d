package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.CreateBooking;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.CreateItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import static ru.practicum.shareit.data.Data.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ShareItTests {

	@Autowired
	private UserService userService;

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private ItemService itemService;

	@Autowired
	private ItemMapper itemMapper;

	@Autowired
	private ItemRequestService itemRequestService;

	@Autowired
	private ItemRequestMapper itemRequestMapper;

	@Autowired
	private BookingService bookingService;

	@Autowired
	private BookingMapper bookingMapper;

	@Autowired
	private ObjectMapper objectMapper;

	private List<UserDto> userDtoList;

	private static List<User> users;

	private List<ItemDto> itemDtoList;

	private static List<Item> items;

	private static List<CreateBooking> createBookingList;

	@Autowired
	TestRestTemplate template;

	@BeforeAll
	static void data() {
		users = generationData(5,User.class);
		items = generationData(8,Item.class,users.get(0),1L);
		createBookingList = generationData(15,CreateBooking.class,1L);

	}

	@BeforeEach
	void start() {
		userDtoList = users.stream().map(user -> userMapper.toUserDto(user)).collect(Collectors.toList());

		itemDtoList = items.stream().map(item -> itemMapper.toItemDto(item)).collect(Collectors.toList());

	}

	@Test
	void testError1() {

	}

	@Order(1)
	@Test
	void addUser() {
		UserDto userDto = userService.addUser(userDtoList.get(0));
		assertNotNull(userDto);
		assertEquals(1,userDto.getId());

	}

	@Order(2)
	@Test
	void upUser() {
		UserDto userDto = userService.addUser(userDtoList.get(0));
		assertEquals("User1",userDto.getName());
		assertEquals("user1@mail",userDto.getEmail());
		UserDto userDtoUp = userDto.builder().name("Пользователь1").email("person@mail.ru").build();
		UserDto newUserDto = userService.upUser(userDtoUp,userDto.getId());
		assertEquals(1,newUserDto.getId());
		assertEquals("Пользователь1",newUserDto.getName());
		assertEquals("person@mail.ru",newUserDto.getEmail());

	}

	@Order(3)
	@Test
	void deleteUserGetUsers() {
		userService.addUser(userDtoList.get(0));
		userService.addUser(userDtoList.get(1));
		userService.addUser(userDtoList.get(2));
		assertIterableEquals(List.of(1L,2L,3L),orderUserId(userService.getUsers(0,5)));
		userService.deleteUser(2);
		assertIterableEquals(List.of(1L,3L),orderUserId(userService.getUsers(0,3)));
		userService.deleteUser(1);
		userService.deleteUser(3);
		assertIterableEquals(List.of(),orderUserId(userService.getUsers(0,4)));
	}

	@Order(4)
	@Test
	void getUser() {
		userService.addUser(userDtoList.get(0));
		UserDto saveUserDto = userService.addUser(userDtoList.get(1));
		separato__________________________________r();
		System.out.println(saveUserDto);
		separato__________________________________r();

		assertNotNull(saveUserDto);
		assertEquals(2,saveUserDto.getId());
		userService.addUser(userDtoList.get(2));
		UserDto userDto = userService.getUser(2);
		assertEquals(2,userDto.getId());
		assertEquals("User2",userDto.getName());
		assertEquals("user2@mail",userDto.getEmail());

	}

	@Order(5)
	@Test
	void getUsers() {
		userService.addUser(userDtoList.get(0));
		userService.addUser(userDtoList.get(1));
		userService.addUser(userDtoList.get(2));
		List<UserDto> userDtos = userService.getUsers(0,10);
		assertEquals(1,userDtos.get(0).getId());
		assertEquals("User1",userDtos.get(0).getName());
		assertEquals("user1@mail",userDtos.get(0).getEmail());
		assertEquals(2,userDtos.get(1).getId());
		assertEquals("User2",userDtos.get(1).getName());
		assertEquals("user2@mail",userDtos.get(1).getEmail());
		assertEquals(3,userDtos.get(2).getId());
		assertEquals("User3",userDtos.get(2).getName());
		assertEquals("user3@mail",userDtos.get(2).getEmail());

	}

	@Order(6)
	@Test
	void addItem() {
		userService.addUser(userDtoList.get(0));

		ItemDto itemDtoNew = itemMapper.toItemDto(items.get(0));
		ItemDto itemDto = itemService.addItem(itemDtoNew,1);
		assertNotNull(itemDto);
		assertEquals(1,itemDto.getId());
	}

	@Order(7)
	@Test
	void upItem() {
		userService.addUser(userDtoList.get(0));
		UserDto userDto = userService.addUser(userDtoList.get(1));

		ItemDto itemDto = itemService.addItem(itemDtoList.get(0),2);

		System.out.println(itemDto);

		ItemDto newItemDto = itemService.upItem(itemDto.toBuilder()
				.name("Молоток")
				.description("Тяжелый молоток")
				.build(),1,2);

		assertEquals(1,newItemDto.getId());
		assertEquals("Молоток",newItemDto.getName());
		assertEquals("Тяжелый молоток",newItemDto.getDescription());
		assertEquals(userDto,newItemDto.getOwner());
	}

	@Order(8)
	@Test
	void getItem() {
		UserDto owner1 = userService.addUser(userDtoList.get(0));
		UserDto owner2 = userService.addUser(userDtoList.get(1));

		itemService.addItem(itemDtoList.get(0),1);
		itemService.addItem(itemDtoList.get(1),2);

		ItemDto itemDto1 = itemService.getItem(1,1);
		assertEquals(1,itemDto1.getId());
		assertEquals("item1",itemDto1.getName());
		assertEquals("описание вещи 1",itemDto1.getDescription());
		assertEquals(owner1,itemDto1.getOwner());

		ItemDto itemDto2 = itemService.getItem(2,2);
		assertEquals(2,itemDto2.getId());
		assertEquals("item2",itemDto2.getName());
		assertEquals("описание вещи 2",itemDto2.getDescription());
		assertEquals(owner2,itemDto2.getOwner());
	}

	@Order(9)
	@Test
	void getItemsByUserId() {

		userMapper.toUser(userService.addUser(userDtoList.get(0)));
		userMapper.toUser(userService.addUser(userDtoList.get(1)));
		userMapper.toUser(userService.addUser(userDtoList.get(2)));

		itemService.addItem(itemDtoList.get(0),1);
		itemService.addItem(itemDtoList.get(1),1);
		itemService.addItem(itemDtoList.get(2),1);
		itemService.addItem(itemDtoList.get(3),1);
		itemService.addItem(itemDtoList.get(4),1);

		assertIterableEquals(List.of(1L,2L,3L,4L,5L),orderItemId(itemService.getItemsByUserId(1,0,10)));

		//заказы на первую вещь, владелиц user 1
		Booking booking1 = bookingService.addBooking(createBookingList.get(0), 3L);
		bookingService.setStatus(booking1.getId(),1, true);
		Booking booking2 = bookingService.addBooking(createBookingList.get(1), 3L);
		bookingService.setStatus(booking2.getId(),1, true);
		Booking booking3 = bookingService.addBooking(createBookingList.get(2), 3L);
		bookingService.setStatus(booking3.getId(),1, true);
		Booking booking4 = bookingService.addBooking(createBookingList.get(3), 3L);
		bookingService.setStatus(booking4.getId(),1, true);
		//заказы на вторую вещь, владелиц user 1
		Booking booking5 = bookingService.addBooking(createBookingList.get(4).toBuilder().itemId(2L).build(), 3L);
		bookingService.setStatus(booking5.getId(),1, true);
		Booking booking6 = bookingService.addBooking(createBookingList.get(5).toBuilder().itemId(2L).build(), 3L);
		bookingService.setStatus(booking6.getId(),1, true);
		Booking booking7 = bookingService.addBooking(createBookingList.get(6).toBuilder().itemId(2L).build(), 3L);
		bookingService.setStatus(booking7.getId(),1, false);
		//заказы на третью вещь, владелиц user 1
		Booking booking8 = bookingService.addBooking(createBookingList.get(7).toBuilder().itemId(3L).build(), 3L);
		bookingService.setStatus(booking8.getId(),1, true);
		Booking booking9 = bookingService.addBooking(createBookingList.get(8).toBuilder().itemId(3L).build(), 3L);
		bookingService.setStatus(booking9.getId(),1, false);
		Booking booking10 = bookingService.addBooking(createBookingList.get(9).toBuilder().itemId(3L).build(), 3L);
		bookingService.setStatus(booking10.getId(),1, false);
		//заказы на четвертую вещь, владелиц user 1
		Booking booking11 = bookingService.addBooking(createBookingList.get(10).toBuilder().itemId(4L).build(), 3L);
		//заказы на пятую вещь, владелиц user 1
		Booking booking12 = bookingService.addBooking(createBookingList.get(11).toBuilder().itemId(5L).build(), 3L);
		bookingService.setStatus(booking12.getId(),1, true);
		Booking booking13 = bookingService.addBooking(createBookingList.get(12).toBuilder().itemId(5L).build(), 3L);
		bookingService.setStatus(booking13.getId(),1, true);
		Booking booking14 = bookingService.addBooking(createBookingList.get(13).toBuilder().itemId(5L).build(), 3L);
		bookingService.setStatus(booking14.getId(),1, true);

		List<ItemDto> itemDtoList1 = itemService.getItemsByUserId(1,0,10);

		assertEquals(5,itemDtoList1.size());
		assertEquals(itemDtoList1.get(0).getLastBooking().getId(),booking2.getId());
		assertEquals(itemDtoList1.get(0).getLastBooking().getBookerId(),3);
		assertEquals(itemDtoList1.get(0).getNextBooking().getId(),booking3.getId());
		assertEquals(itemDtoList1.get(0).getNextBooking().getBookerId(),3);

		assertEquals(itemDtoList1.get(1).getLastBooking().getId(),booking5.getId());
		assertEquals(itemDtoList1.get(1).getLastBooking().getBookerId(),3);
		assertEquals(itemDtoList1.get(1).getNextBooking().getId(),booking6.getId());
		assertEquals(itemDtoList1.get(1).getNextBooking().getBookerId(),3);

		assertEquals(itemDtoList1.get(2).getLastBooking().getId(),booking8.getId());
		assertEquals(itemDtoList1.get(2).getLastBooking().getBookerId(),3);
		assertNull(itemDtoList1.get(2).getNextBooking());

		assertNull(itemDtoList1.get(3).getLastBooking());
		assertNull(itemDtoList1.get(3).getNextBooking());

		assertEquals(itemDtoList1.get(4).getLastBooking().getId(),booking12.getId());
		assertEquals(itemDtoList1.get(4).getLastBooking().getBookerId(),3);
		assertEquals(itemDtoList1.get(4).getNextBooking().getId(),booking13.getId());
		assertEquals(itemDtoList1.get(4).getNextBooking().getBookerId(),3);
	}

	@Order(10)
	@Test
	void addComment() {
		userMapper.toUser(userService.addUser(userDtoList.get(0)));
		userMapper.toUser(userService.addUser(userDtoList.get(1)));
		userMapper.toUser(userService.addUser(userDtoList.get(2)));

		itemService.addItem(itemDtoList.get(0),1);
		itemService.addItem(itemDtoList.get(1),2);
		itemService.addItem(itemDtoList.get(2),3);

		CreateBooking createBooking1 = CreateBooking.builder()
				.itemId(1L)
				.start(LocalDateTime.now().plus(20L, ChronoUnit.MILLIS))
				.end(LocalDateTime.now().plus(30L, ChronoUnit.MILLIS))
				.build();

		bookingService.addBooking(createBooking1, 2L);
		assertEquals(Status.APPROVED,bookingService.setStatus(1,1,true).getStatus());

		CreateBooking createBooking2 = createBooking1.toBuilder()
				.start(LocalDateTime.now().plus(20L, ChronoUnit.MILLIS))
				.end(LocalDateTime.now().plus(30L, ChronoUnit.MILLIS))
				.build();
		bookingService.addBooking(createBooking2, 3L);
		assertEquals(Status.APPROVED,bookingService.setStatus(2,1,true).getStatus());

		sleep(30);
		CommentDto c1 = itemService.addComment(new CreateCommentDto(1L,"комментарий 1"),1,2);
		CommentDto c2 = itemService.addComment(new CreateCommentDto(2L,"комментарий 2"),1,2);
		CommentDto c3 = itemService.addComment(new CreateCommentDto(3L,"комментарий 3"),1,3);

		assertEquals(1,c1.getId());
		assertEquals("User2",c1.getAuthorName());
		//assertEquals(1,c1.getItem().getId());
		assertEquals("комментарий 1",c1.getText());

		assertEquals(2,c2.getId());
		assertEquals("User2",c2.getAuthorName());
		//assertEquals(1,c2.getItem().getId());
		assertEquals("комментарий 2",c2.getText());

		assertEquals(3,c3.getId());
		assertEquals("User3",c3.getAuthorName());
		//assertEquals(1,c3.getItem().getId());
		assertEquals("комментарий 3",c3.getText());

	}

	@Order(11)
	@Test
	void getItemByRequestUsers() {
		long userId1 = userMapper.toUser(userService.addUser(userDtoList.get(0))).getId();
		long userId2 = userMapper.toUser(userService.addUser(userDtoList.get(1))).getId();
		long userId3 = userMapper.toUser(userService.addUser(userDtoList.get(2))).getId();
		long userId4 = userMapper.toUser(userService.addUser(userDtoList.get(3))).getId();
		long userId5 = userMapper.toUser(userService.addUser(userDtoList.get(4))).getId();

		ItemDto itemDto = itemService.addItem(itemDtoList.get(0).toBuilder().requestId(userId1).build(),userId2);
		itemService.addItem(itemDtoList.get(1).toBuilder().requestId(null).build(),userId2);
		itemService.addItem(itemDtoList.get(2).toBuilder().requestId(userId3).build(),userId1);

		createBooking(itemDto.getId(),itemDto.getOwner().getId(),userId1);
		Booking booking2 = createBooking(itemDto.getId(),itemDto.getOwner().getId(),userId3);
		createBooking(itemDto.getId(),itemDto.getOwner().getId(),userId4);
		Booking booking4 = createBooking(itemDto.getId(),itemDto.getOwner().getId(),userId5);
		createBooking(itemDto.getId(),itemDto.getOwner().getId(),userId5);

		sleep(2000);
		CommentDto c1 = itemService.addComment(new CreateCommentDto(1L,"комментарий 1"),1,1);
		CommentDto c2 = itemService.addComment(new CreateCommentDto(2L,"комментарий 2"),1,1);
		CommentDto c3 = itemService.addComment(new CreateCommentDto(3L,"комментарий 3"),1,3);

		ItemDto itemDto12 = itemService.getItemByRequestUsers(1,2);
		assertEquals(1,itemDto12.getId());
		List<CommentDto> commentDtoList = itemDto12.getComments();
		assertEquals(c1.getText(),commentDtoList.get(0).getText());
		assertEquals(c2.getText(),commentDtoList.get(1).getText());
		assertEquals(c3.getText(),commentDtoList.get(2).getText());

		assertEquals(booking2.getId(),itemDto12.getLastBooking().getId());
		assertEquals(booking2.getBooker().getId(),itemDto12.getLastBooking().getBookerId());
		assertEquals(booking4.getId(),itemDto12.getNextBooking().getId());
		assertEquals(booking4.getBooker().getId(),itemDto12.getNextBooking().getBookerId());

		ItemDto itemDto22 = itemService.getItemByRequestUsers(2,2);
		assertEquals(2,itemDto22.getId());
		ItemDto itemDto23 = itemService.getItemByRequestUsers(2,3);
		System.out.println(itemDto23);

	}

	private Booking createBooking(long itemId, long ownerId, long bookerId) {
		CreateBooking createBooking = CreateBooking.builder()
				.itemId(itemId)
				.start(LocalDateTime.now().plus(20L, ChronoUnit.MILLIS))
				.end(LocalDateTime.now().plus(30L, ChronoUnit.MILLIS))
				.build();

		Booking booking = bookingService.addBooking(createBooking, bookerId);
		assertEquals(Status.APPROVED,bookingService.setStatus(booking.getId(),ownerId,true).getStatus());
		return booking;
	}

	@Order(12)
	@Test
	void searchItems() {
		userMapper.toUser(userService.addUser(userDtoList.get(0)));
		userMapper.toUser(userService.addUser(userDtoList.get(1)));

		itemService.addItem(itemDtoList.get(0),1L);
		itemService.addItem(itemDtoList.get(1),1L);
		itemService.addItem(itemDtoList.get(2),1L);
		itemService.addItem(itemDtoList.get(4).toBuilder().description("Дрель аккумуляторная").build(), 1L);

		List<ItemDto> itemDtos1 = itemService.search("ОпИс",2L,0,10);
		assertEquals(3,itemDtos1.size());
		List<ItemDto> itemDtos2 = itemService.search("ДРе",2L,0,10);
		assertEquals(1,itemDtos2.size());
	}

	@Order(13)
	@Test
	void addBooking() {
		userService.addUser(userDtoList.get(0));
		User booker = userMapper.toUser(userService.addUser(userDtoList.get(1)));

		itemService.addItem(itemDtoList.get(0),1);

		Booking booking = bookingService.addBooking(createBookingList.get(0),2L);
		assertNotNull(booking);
		assertEquals(1,booking.getId());
		assertEquals(booker,booking.getBooker());
		assertEquals(Status.WAITING,booking.getStatus());
	}

	@Order(14)
	@Test
	void setStatus() {
		User owner = userMapper.toUser(userService.addUser(userDtoList.get(0)));
		User booker = userMapper.toUser(userService.addUser(userDtoList.get(1)));

		itemService.addItem(itemDtoList.get(0),1);

		Booking booking = bookingService.addBooking(createBookingList.get(0),booker.getId());
		assertNotNull(booking);

		Booking bookingRejected = bookingService.setStatus(booking.getId(),owner.getId(),false);
		assertEquals(Status.REJECTED,bookingRejected.getStatus());

	}

	@Order(15)
	@Test
	void getBookingByIdForUserId() {
		userService.addUser(userDtoList.get(3));
		userService.addUser(userDtoList.get(2));

		itemService.addItem(itemDtoList.get(0),1);
		itemService.addItem(itemDtoList.get(1),1);
		itemService.addItem(itemDtoList.get(2),1);


		bookingService.addBooking(createBookingList.get(0).toBuilder().itemId(1L).build(), 2L);
		bookingService.addBooking(createBookingList.get(1).toBuilder().itemId(2L).build(),2L);
		bookingService.addBooking(createBookingList.get(2).toBuilder().itemId(3L).build(),2L);


		Booking booking1 = bookingService.getBookingByIdForUserId(1L,2L);
		assertNotNull(booking1);
		assertEquals(1,booking1.getId());
		Booking booking2 = bookingService.getBookingByIdForUserId(2L,2L);
		assertNotNull(booking2);
		assertEquals(2,booking2.getId());
		Booking booking3 = bookingService.getBookingByIdForUserId(3L,2L);
		assertNotNull(booking3);
		assertEquals(3,booking3.getId());

		Booking booking31 = bookingService.getBookingByIdForUserId(3L,1L);
		assertNotNull(booking31);
		assertEquals(3,booking31.getId());
	}

	@Order(16)
	@Test
	void getBookingsForBooker() {
		userService.addUser(userDtoList.get(0));
		userService.addUser(userDtoList.get(1));

		itemService.addItem(itemDtoList.get(0),1);
		itemService.addItem(itemDtoList.get(1),1);
		itemService.addItem(itemDtoList.get(2),1);
		itemService.addItem(itemDtoList.get(3),1);
		itemService.addItem(itemDtoList.get(4),1);

		bookingService.addBooking(createBookingList.get(0).toBuilder().itemId(1L).build(),2L);
		bookingService.addBooking(createBookingList.get(1).toBuilder().itemId(2L).build(),2L);
		bookingService.addBooking(createBookingList.get(2).toBuilder().itemId(3L).build(),2L);
		bookingService.addBooking(createBookingList.get(3).toBuilder().itemId(4L).build(),2L);
		bookingService.addBooking(createBookingList.get(4).toBuilder().itemId(5L).build(),2L);

		List<Booking> bookings1 = bookingService.getBookingsForBooker(2L,DEFAULT,0,10);
		assertEquals(5,bookings1.size());
		assertIterableEquals(List.of(5L,4L,3L,2L,1L),orderBookingId(bookings1));
		List<Booking> bookings2 = bookingService.getBookingsForBooker(2L,DEFAULT,1,10);
		assertEquals(4,bookings2.size());
		List<Booking> bookings3 = bookingService.getBookingsForBooker(2L,DEFAULT,2,2);
		assertEquals(2,bookings3.size());
		assertEquals(3,bookings3.get(0).getId());
		assertEquals(2,bookings3.get(1).getId());
		List<Booking> bookings4 = bookingService.getBookingsForBooker(2L,DEFAULT,4,2);
		assertEquals(1,bookings4.size());
		assertEquals(1,bookings4.get(0).getId());

		List<Booking> bookingsAll1 = bookingService.getBookingsForBooker(2L,ALL,0,10);
		assertIterableEquals(List.of(5L,4L,3L,2L,1L),orderBookingId(bookingsAll1));
		List<Booking> bookingsFuture1 = bookingService.getBookingsForBooker(2L,FUTURE,0,10);
		assertIterableEquals(List.of(5L,4L,3L,2L,1L),orderBookingId(bookingsFuture1));
		List<Booking> bookingsWaiting1 = bookingService.getBookingsForBooker(2L,WAITING,0,10);
		assertIterableEquals(List.of(5L,4L,3L,2L,1L),orderBookingId(bookingsWaiting1));
		List<Booking> bookingsRejected1 = bookingService.getBookingsForBooker(2L,REJECTED,0,10);
		assertIterableEquals(List.of(),orderBookingId(bookingsRejected1));
		bookingService.setStatus(1,1,false);
		bookingService.setStatus(5,1,false);
		bookingsRejected1 = bookingService.getBookingsForBooker(2L,REJECTED,0,10);
		assertIterableEquals(List.of(5L,1L),orderBookingId(bookingsRejected1));
		List<Booking> bookingsCurrent1 = bookingService.getBookingsForBooker(2L,CURRENT,0,10);
		assertIterableEquals(List.of(),orderBookingId(bookingsCurrent1));


		itemService.addItem(itemDtoList.get(5),1);
		itemService.addItem(itemDtoList.get(6),1);
		itemService.addItem(itemDtoList.get(7),1);

		bookingService.addBooking(createBookingList.get(5).toBuilder()
				.start(LocalDateTime.now().plusSeconds(1))
				.end(LocalDateTime.now().plusSeconds(2))
				.itemId(6L).build(),2L);
		bookingService.addBooking(createBookingList.get(6).toBuilder()
				.start(LocalDateTime.now().plusSeconds(1))
				.end(LocalDateTime.now().plusSeconds(2))
				.itemId(7L).build(),2L);
		sleep(1100);
		bookingsCurrent1 = bookingService.getBookingsForBooker(2L,CURRENT,0,10);
		assertIterableEquals(List.of(6L,7L),orderBookingId(bookingsCurrent1));
		sleep(1100);
		List<Booking> bookingsPast1 = bookingService.getBookingsForBooker(2L,PAST,0,10);
		assertIterableEquals(List.of(7L,6L),orderBookingId(bookingsPast1));

	}

	@Order(17)
	@Test
	void getBookingsForOwnerItems() {
		userService.addUser(userDtoList.get(0));
		userService.addUser(userDtoList.get(1));

		itemService.addItem(itemDtoList.get(0),1);
		itemService.addItem(itemDtoList.get(1),1);
		itemService.addItem(itemDtoList.get(2),1);

		bookingService.addBooking(createBookingList.get(0).toBuilder().itemId(1L).build(),2L);
		bookingService.addBooking(createBookingList.get(1).toBuilder().itemId(2L).build(),2L);
		bookingService.addBooking(createBookingList.get(2).toBuilder().itemId(3L).build(),2L);

		List<Booking> bookings1 = bookingService.getBookingsOwnerState(1L,DEFAULT,0,10);
		assertEquals(3,bookings1.size());
		assertIterableEquals(List.of(3L,2L,1L),orderBookingId(bookings1));
		List<Booking> bookings2 = bookingService.getBookingsOwnerState(1L,DEFAULT,1,10);
		assertEquals(2,bookings2.size());
		List<Booking> bookings3 = bookingService.getBookingsOwnerState(1L,DEFAULT,2,2);
		assertEquals(1,bookings3.size());
		assertEquals(1,bookings3.get(0).getId());

		List<Booking> bookingsAll1 = bookingService.getBookingsOwnerState(1L,ALL,0,10);
		assertIterableEquals(List.of(3L,2L,1L),orderBookingId(bookingsAll1));
		List<Booking> bookingsFuture1 = bookingService.getBookingsOwnerState(1L,FUTURE,0,10);
		assertIterableEquals(List.of(3L,2L,1L),orderBookingId(bookingsFuture1));
		List<Booking> bookingsWaiting1 = bookingService.getBookingsOwnerState(1L,WAITING,0,10);
		assertIterableEquals(List.of(3L,2L,1L),orderBookingId(bookingsWaiting1));
		List<Booking> bookingsRejected1 = bookingService.getBookingsOwnerState(1L,REJECTED,0,10);
		assertIterableEquals(List.of(),orderBookingId(bookingsRejected1));
		bookingService.setStatus(1,1,false);
		bookingService.setStatus(3,1,false);
		bookingsRejected1 = bookingService.getBookingsOwnerState(1L,REJECTED,0,10);
		assertIterableEquals(List.of(3L,1L),orderBookingId(bookingsRejected1));
		List<Booking> bookingsCurrent1 = bookingService.getBookingsOwnerState(1L,CURRENT,0,10);
		assertIterableEquals(List.of(),orderBookingId(bookingsCurrent1));

		itemService.addItem(itemDtoList.get(4),1);
		itemService.addItem(itemDtoList.get(5),1);
		itemService.addItem(itemDtoList.get(6),1);

		bookingService.addBooking(createBookingList.get(3).toBuilder()
				.start(LocalDateTime.now().plusSeconds(1))
				.end(LocalDateTime.now().plusSeconds(2))
				.itemId(4L).build(),2L);
		bookingService.addBooking(createBookingList.get(4).toBuilder()
				.start(LocalDateTime.now().plusSeconds(1))
				.end(LocalDateTime.now().plusSeconds(2))
				.itemId(5L).build(),2L);
		sleep(1100);
		bookingsCurrent1 = bookingService.getBookingsOwnerState(1L,CURRENT,0,10);
		assertIterableEquals(List.of(4L,5L),orderBookingId(bookingsCurrent1));
		sleep(1100);
		List<Booking> bookingsPast1 = bookingService.getBookingsOwnerState(1L,PAST,0,10);
		assertIterableEquals(List.of(5L,4L),orderBookingId(bookingsPast1));

	}

	@Order(18)
	@Test
	void addItemRequest() {
		userService.addUser(userDtoList.get(0));

	ItemRequestDto ird = itemRequestService.addItemRequest(new CreateItemRequest(0,"Дрель"),1L);
		assertNotNull(ird);
		assertEquals(1,ird.getId());
		assertEquals("Дрель",ird.getDescription());
		assertEquals(1,ird.getRequester());
		assertIterableEquals(List.of(),ird.getItems());

	}

	@Order(19)
	@Test
	void getItemsRequester() {
		userService.addUser(userDtoList.get(0));
		userService.addUser(userDtoList.get(1));
		userService.addUser(userDtoList.get(2));

		itemRequestService.addItemRequest(new CreateItemRequest(0,"Дрель"),1L);
		itemRequestService.addItemRequest(new CreateItemRequest(0,"Пила"),1L);
		itemRequestService.addItemRequest(new CreateItemRequest(0,"Чайник"),2L);
		itemRequestService.addItemRequest(new CreateItemRequest(0,"Клюшка"),3L);

		ItemDto itemDto1 = itemService.addItem(itemDtoList.get(0).toBuilder()
				.name("Дрель")
				.description("Дрель бытовая")
				.requestId(1L)
				.build(),2);

		ItemDto itemDto2 = itemService.addItem(itemDtoList.get(1).toBuilder()
				.name("Дрель")
				.description("Дрель професиональная")
				.requestId(1L)
				.build(),3);

		ItemDto itemDto3 = itemService.addItem(itemDtoList.get(2).toBuilder()
				.name("Дрель")
				.description("Дрель 3")
				.requestId(1L)
				.build(),3);

		List<ItemRequestDto> itemRequestDtoList = itemRequestService.getItemsRequester(1);
		assertEquals(2,itemRequestDtoList.size());
		assertEquals(List.of(itemDto1,itemDto2,itemDto3),itemRequestDtoList.get(0).getItems());

	}

	@Order(20)
	@Test
	void getItemsRequesterPaginationAndGetItemRequestByIdForOtherUser() {
		userService.addUser(userDtoList.get(0));
		userService.addUser(userDtoList.get(1));
		userService.addUser(userDtoList.get(2));

		itemRequestService.addItemRequest(new CreateItemRequest(0,"Дрель"),1L);
		itemRequestService.addItemRequest(new CreateItemRequest(0,"Пила"),1L);
		itemRequestService.addItemRequest(new CreateItemRequest(0,"Чайник"),1L);
		itemRequestService.addItemRequest(new CreateItemRequest(0,"Клюшка"),3L);
		itemRequestService.addItemRequest(new CreateItemRequest(0,"Клюшка"),3L);

		ItemDto itemDto1 = itemService.addItem(itemDtoList.get(0).toBuilder()
				.name("Дрель")
				.description("Дрель бытовая")
				.requestId(1L)
				.build(),2);

		ItemDto itemDto2 = itemService.addItem(itemDtoList.get(1).toBuilder()
				.name("Дрель")
				.description("Дрель професиональная")
				.requestId(1L)
				.build(),3);

		ItemDto itemDto3 = itemService.addItem(itemDtoList.get(2).toBuilder()
				.name("Дрель")
				.description("Дрель 3")
				.requestId(1L)
				.build(),3);

		List<ItemRequestDto> itemRequestDtoList1 = itemRequestService.getItemsRequesterPagination(2,0,10);
		assertEquals(5,itemRequestDtoList1.size());
		assertEquals(List.of(itemDto1,itemDto2,itemDto3),itemRequestDtoList1.get(0).getItems());

		List<ItemRequestDto> itemRequestDtoList2 = itemRequestService.getItemsRequesterPagination(2,1,6);
		assertEquals(4,itemRequestDtoList2.size());

		ItemRequestDto itemRequestDto1 = itemRequestService.getItemRequestByIdForOtherUser(2,1);
		assertEquals(1,itemRequestDto1.getId());
		assertEquals(List.of(itemDto1,itemDto2,itemDto3),itemRequestDto1.getItems());

		ItemRequestDto itemRequestDto2 = itemRequestService.getItemRequestByIdForOtherUser(2,2);
		assertEquals(2,itemRequestDto2.getId());
		assertEquals(List.of(),itemRequestDto2.getItems());
	}


	@Order(21)
	@Test
	void testError() throws Exception {

		/*ResponseEntity<User> response = template.postForEntity("/users",UserDto.builder().build(), User.class);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());*/

		ResponseEntity<User> response1 = template.getForEntity("/users/99", User.class);
		assertEquals(HttpStatus.NOT_FOUND, response1.getStatusCode());

		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.set("X-Sharer-User-Id", "1");

		HttpEntity entity = new HttpEntity(headers);

		userService.addUser(userDtoList.get(0));

		/*ResponseEntity<Booking> response2 = template.exchange("/bookings?state={state}",
				HttpMethod.GET,
				entity,
				Booking.class,
				"UNSUPPORTED_STATUS"
		);
		assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());*/

		userService.addUser(userDtoList.get(1));

		ItemDto itemDtoNew = itemDtoList.get(0).toBuilder()
				.available(false)
				.build();

		itemService.addItem(itemDtoNew,2);

		HttpHeaders headersBooking = new HttpHeaders();
		headersBooking.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headersBooking.setContentType(MediaType.APPLICATION_JSON);
		headersBooking.set("X-Sharer-User-Id", "1");


		String body = objectMapper.writeValueAsString(createBookingList.get(0));
		HttpEntity<String> request = new HttpEntity<>(body,headersBooking);
		ResponseEntity<Booking> response3 = template.postForEntity("/bookings",
				request,
				Booking.class);

		assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());

		ResponseEntity<String> response4 = template.postForEntity("/bookings",
				new HttpEntity<>(body,new HttpHeaders()),
				String.class);

		assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, response4.getStatusCode());
	}

	private List<Long> orderBookingId(List<Booking> objects) {
		return objects.stream().map(b -> b.getId()).collect(Collectors.toList());
	}

	private List<Long> orderUserId(List<UserDto> users) {
		return users.stream().map(u -> u.getId()).collect(Collectors.toList());
	}

	private List<Long> orderItemId(List<ItemDto> items) {
		return items.stream().map(i -> i.getId()).collect(Collectors.toList());
	}

	private void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			System.out.println(e);
		}
	}
}
